package com.aegisqa.browser.selector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages a registry of named selectors with their strategy.
 * Supports runtime healing by updating selectors when AI suggests alternatives.
 */
@Slf4j
@Component
public class SelectorRegistry {

    private final Map<String, SelectorEntry> registry = new ConcurrentHashMap<>();

    /**
     * Register a named selector.
     */
    public void register(String name, String selector, SelectorStrategy strategy) {
        registry.put(name, new SelectorEntry(selector, strategy));
        log.debug("Registered selector '{}': {} ({})", name, selector, strategy);
    }

    /**
     * Look up a selector by name.
     */
    public SelectorEntry get(String name) {
        SelectorEntry entry = registry.get(name);
        if (entry == null) {
            log.warn("Selector '{}' not found in registry", name);
        }
        return entry;
    }

    /**
     * Heal a broken selector with an AI-suggested alternative.
     * Logs the change for auditability.
     */
    public void heal(String name, String newSelector, SelectorStrategy strategy, String reason) {
        SelectorEntry old = registry.get(name);
        registry.put(name, new SelectorEntry(newSelector, strategy));
        log.info("Selector healed: '{}' from '{}' to '{}' (reason: {})",
                name, old != null ? old.selector() : "unknown", newSelector, reason);
    }

    public record SelectorEntry(String selector, SelectorStrategy strategy) {
        public String toPlaywrightLocator() {
            return switch (strategy) {
                case CSS -> selector;
                case XPATH -> "xpath=" + selector;
                case TEXT -> "text=" + selector;
                case ROLE -> "role=" + selector;
                case LABEL -> "label=" + selector;
                case TEST_ID -> "[data-testid='" + selector + "']";
                case PLACEHOLDER -> "[placeholder='" + selector + "']";
            };
        }
    }
}
