package com.aegisqa.browser.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core service for managing Playwright browser sessions and executing actions.
 * Each execution gets its own BrowserContext for isolation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BrowserExecutorService {

    private final Browser browser;

    @Value("${aegisqa.browser.timeout-ms:30000}")
    private int defaultTimeoutMs;

    @Value("${aegisqa.reports.dir:reports}")
    private String reportsDir;

    private final Map<String, BrowserContext> contexts = new ConcurrentHashMap<>();

    /**
     * Open a new browser session for an execution.
     */
    public String openSession(String executionId) {
        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setRecordVideoDir(Paths.get(reportsDir + "/videos/" + executionId))
        );
        context.setDefaultTimeout(defaultTimeoutMs);
        contexts.put(executionId, context);
        log.info("Browser session opened for execution: {}", executionId);
        return executionId;
    }

    /**
     * Get or create a page for an execution session.
     */
    public Page getPage(String executionId) {
        BrowserContext context = contexts.get(executionId);
        if (context == null) {
            openSession(executionId);
            context = contexts.get(executionId);
        }
        if (context.pages().isEmpty()) {
            return context.newPage();
        }
        return context.pages().get(0);
    }

    /**
     * Navigate to a URL.
     */
    public void navigate(String executionId, String url) {
        Page page = getPage(executionId);
        log.info("[{}] Navigating to: {}", executionId, url);
        page.navigate(url);
        page.waitForLoadState();
    }

    /**
     * Click an element, waiting for it to become visible first.
     */
    public void click(String executionId, String selector) {
        log.debug("[{}] Click: {}", executionId, selector);
        Page page = getPage(executionId);
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        page.click(selector);
    }

    /**
     * Fill a form field, waiting for the element to become visible first.
     */
    public void fill(String executionId, String selector, String value) {
        log.debug("[{}] Fill: {}", executionId, selector);
        Page page = getPage(executionId);
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        page.fill(selector, value);
    }

    /**
     * Capture a screenshot and return the file path.
     */
    public String screenshot(String executionId, String label) {
        String path = reportsDir + "/screenshots/" + executionId + "/" + label + "_" +
                System.currentTimeMillis() + ".png";
        Paths.get(path).getParent().toFile().mkdirs();
        getPage(executionId).screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
        log.info("[{}] Screenshot: {}", executionId, path);
        return path;
    }

    /**
     * Close the browser session for an execution.
     */
    public void closeSession(String executionId) {
        BrowserContext ctx = contexts.remove(executionId);
        if (ctx != null) {
            ctx.close();
            log.info("Browser session closed for execution: {}", executionId);
        }
    }
}
