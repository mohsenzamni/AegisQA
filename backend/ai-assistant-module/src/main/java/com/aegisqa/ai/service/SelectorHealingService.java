package com.aegisqa.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * AI-assisted selector healing.
 * When a Playwright selector fails, asks the AI to suggest alternative selectors.
 * The executor validates the suggestion before applying it.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SelectorHealingService {

    private final ChatClient chatClient;

    /**
     * Suggest an alternative selector for a broken one.
     *
     * @param brokenSelector the selector that failed
     * @param pageSource     the HTML source of the current page (should be sanitized)
     * @param context        additional context about what element is expected
     * @return suggested selector string, or null if unavailable
     */
    public String suggestAlternative(String brokenSelector, String pageSource, String context) {
        log.info("AI healing selector: {} (context: {})", brokenSelector, context);
        try {
            // Truncate page source to avoid token overflow
            String truncatedSource = pageSource != null && pageSource.length() > 3000
                    ? pageSource.substring(0, 3000) + "... [truncated]"
                    : pageSource;

            String prompt = """
                    A Playwright selector failed to find an element. Suggest an alternative.
                    
                    Broken selector: %s
                    Expected element context: %s
                    
                    Page HTML (excerpt):
                    %s
                    
                    Respond ONLY with a single alternative CSS selector string, nothing else.
                    Example: button[data-testid='create-chain']
                    """.formatted(brokenSelector, context, truncatedSource);

            String suggestion = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.info("AI selector suggestion for '{}': '{}'", brokenSelector, suggestion);
            return suggestion != null ? suggestion.trim() : null;
        } catch (Exception e) {
            log.warn("Selector healing AI unavailable: {}", e.getMessage());
            return null;
        }
    }
}
