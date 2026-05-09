package com.aegisqa.browser.action;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Clicks a UI element identified by a selector or visible text.
 */
@Slf4j
@Component
public class ClickAction implements BrowserAction {

    @Override
    public String getName() {
        return "click";
    }

    @Override
    public void execute(Page page, Map<String, Object> params) {
        String selector = getRequired(params, "selector");
        log.debug("Clicking element: {}", selector);
        page.click(selector);
        log.debug("Clicked: {}", selector);
    }

    private String getRequired(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val == null) throw new IllegalArgumentException("Missing required parameter: " + key);
        return val.toString();
    }
}
