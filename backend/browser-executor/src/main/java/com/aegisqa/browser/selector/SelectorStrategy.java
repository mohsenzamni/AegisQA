package com.aegisqa.browser.selector;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Selector strategy types supported by AegisQA browser executor.
 */
@Getter
@RequiredArgsConstructor
public enum SelectorStrategy {
    CSS("CSS selector"),
    XPATH("XPath expression"),
    TEXT("Visible text content"),
    ROLE("ARIA role"),
    LABEL("Form field label"),
    TEST_ID("data-testid attribute"),
    PLACEHOLDER("Input placeholder text");

    private final String description;
}
