package com.aegisqa.recovery.detector;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Detects blocking modal dialogs that prevent test execution.
 */
@Slf4j
@Component
public class ModalDetector implements RecoveryDetector {

    private static final String[] MODAL_SELECTORS = {
            "[role='dialog']",
            ".modal:visible",
            ".modal-dialog:visible",
            "[data-testid='modal']:visible",
            ".overlay:visible"
    };

    @Override
    public boolean detect(Page page) {
        for (String selector : MODAL_SELECTORS) {
            try {
                if (page.locator(selector).isVisible()) {
                    log.info("Modal detected with selector: {}", selector);
                    return true;
                }
            } catch (Exception e) {
                // Continue checking other selectors
            }
        }
        return false;
    }

    @Override
    public RecoveryAction suggestedAction() {
        return RecoveryAction.DISMISS_MODAL;
    }

    @Override
    public String description() {
        return "Detects blocking modal dialogs";
    }
}
