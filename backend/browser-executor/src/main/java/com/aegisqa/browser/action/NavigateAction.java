package com.aegisqa.browser.action;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Navigates the browser to a given URL.
 */
@Slf4j
@Component
public class NavigateAction implements BrowserAction {

    @Override
    public String getName() {
        return "navigate";
    }

    @Override
    public void execute(Page page, Map<String, Object> params) {
        String url = getRequired(params, "url");
        log.info("Navigating to: {}", url);
        page.navigate(url);
        page.waitForLoadState();
        log.debug("Navigation complete to: {}", url);
    }

    private String getRequired(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val == null) throw new IllegalArgumentException("Missing required parameter: " + key);
        return val.toString();
    }
}
