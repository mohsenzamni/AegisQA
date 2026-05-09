package com.aegisqa.browser.action;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fills a form field with a value.
 */
@Slf4j
@Component
public class FillAction implements BrowserAction {

    @Override
    public String getName() {
        return "fill";
    }

    @Override
    public void execute(Page page, Map<String, Object> params) {
        String selector = getRequired(params, "selector");
        String value = getRequired(params, "value");
        log.debug("Filling '{}' with value (masked)", selector);
        page.fill(selector, value);
    }

    private String getRequired(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val == null) throw new IllegalArgumentException("Missing required parameter: " + key);
        return val.toString();
    }
}
