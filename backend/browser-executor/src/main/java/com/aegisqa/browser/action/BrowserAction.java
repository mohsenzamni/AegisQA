package com.aegisqa.browser.action;

import com.microsoft.playwright.Page;

import java.util.Map;

/**
 * A single, deterministic browser action that can be executed on a Playwright Page.
 */
public interface BrowserAction {

    /**
     * The canonical name of this action (e.g. "click", "fill").
     */
    String getName();

    /**
     * Execute this action on the given page with the provided parameters.
     *
     * @param page   the current Playwright page
     * @param params action-specific parameters
     */
    void execute(Page page, Map<String, Object> params);
}
