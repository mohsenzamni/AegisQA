package com.aegisqa.browser.action;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Waits for text to appear in the page or for a URL pattern.
 */
@Slf4j
@Component
public class WaitAction implements BrowserAction {

    @Override
    public String getName() {
        return "wait";
    }

    @Override
    public void execute(Page page, Map<String, Object> params) {
        if (params.containsKey("text")) {
            String text = params.get("text").toString();
            log.debug("Waiting for text: {}", text);
            page.waitForSelector("text=" + text);
        } else if (params.containsKey("selector")) {
            String selector = params.get("selector").toString();
            log.debug("Waiting for selector: {}", selector);
            page.waitForSelector(selector);
        } else if (params.containsKey("ms")) {
            long ms = Long.parseLong(params.get("ms").toString());
            log.debug("Waiting {}ms", ms);
            page.waitForTimeout(ms);
        } else {
            page.waitForLoadState();
        }
    }
}
