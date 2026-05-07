package com.aegisqa.recovery.detector;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Detects loading spinners / stalled page loads.
 */
@Slf4j
@Component
public class LoadingStallDetector implements RecoveryDetector {

    private static final String[] LOADING_INDICATORS = {
            ".spinner:visible",
            ".loading:visible",
            "[data-testid='loading']:visible",
            ".progress-bar:visible"
    };

    @Override
    public boolean detect(Page page) {
        for (String indicator : LOADING_INDICATORS) {
            try {
                if (page.locator(indicator).isVisible()) {
                    log.warn("Loading stall detected: {}", indicator);
                    return true;
                }
            } catch (Exception e) {
                // Continue
            }
        }
        return false;
    }

    @Override
    public RecoveryAction suggestedAction() {
        return RecoveryAction.WAIT_AND_RETRY;
    }

    @Override
    public String description() {
        return "Detects page loading stalls";
    }
}
