package com.aegisqa.browser.page;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * Base class for all Page Object Model implementations.
 * Provides common utilities: screenshot, wait, navigation.
 */
@Slf4j
public abstract class PageObject {

    @Getter
    protected final Page page;

    protected PageObject(Page page) {
        this.page = page;
    }

    /**
     * Capture a screenshot and return the file path.
     */
    public String screenshot(String label) {
        String filename = "screenshots/" + label + "_" + Instant.now().toEpochMilli() + ".png";
        Path path = Paths.get(filename);
        path.getParent().toFile().mkdirs();
        page.screenshot(new Page.ScreenshotOptions().setPath(path));
        log.info("Screenshot captured: {}", filename);
        return filename;
    }

    /**
     * Wait for the page to reach network idle state.
     */
    public void waitForIdle() {
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    /**
     * Wait for a text string to appear anywhere on the page.
     */
    public void waitForText(String text) {
        page.waitForSelector("text=" + text);
    }

    /**
     * Get visible text of element matching the selector.
     */
    public String getText(String selector) {
        return page.locator(selector).innerText();
    }

    /**
     * Check if an element matching selector is visible.
     */
    public boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    /**
     * Click an element, waiting for it to become visible first.
     */
    public void click(String selector) {
        log.debug("Click: {}", selector);
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        page.click(selector);
    }

    /**
     * Fill a form field, waiting for the element to become visible first.
     */
    public void fill(String selector, String value) {
        log.debug("Fill: {} = [MASKED]", selector);
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        page.fill(selector, value);
    }
}
