package com.aegisqa.recovery.strategy;

import com.aegisqa.recovery.detector.RecoveryDetector;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default recovery strategy that handles the most common recovery scenarios.
 */
@Slf4j
@Component
public class DefaultRecoveryStrategy implements RecoveryStrategy {

    @Override
    public RecoveryDetector.RecoveryAction handles() {
        return RecoveryDetector.RecoveryAction.DISMISS_MODAL;
    }

    @Override
    public void recover(Page page, String executionId) {
        log.info("[{}] Attempting to dismiss modal", executionId);

        // Try common dismiss patterns
        String[] dismissSelectors = {
                "button:has-text('Close')",
                "button:has-text('Cancel')",
                "button:has-text('OK')",
                "[aria-label='Close']",
                ".modal-close",
                ".btn-close"
        };

        for (String sel : dismissSelectors) {
            try {
                Locator btn = page.locator(sel);
                if (btn.isVisible()) {
                    btn.click();
                    log.info("[{}] Modal dismissed using: {}", executionId, sel);
                    page.waitForLoadState();
                    return;
                }
            } catch (Exception e) {
                // Try next selector
            }
        }

        // Fallback: press Escape
        log.info("[{}] Dismissing modal via Escape key", executionId);
        page.keyboard().press("Escape");
    }
}
