package com.aegisqa.recovery.detector;

import com.microsoft.playwright.Page;

/**
 * Strategy interface for detecting UI recovery scenarios.
 */
public interface RecoveryDetector {

    /**
     * Check if this detector's condition is present on the page.
     *
     * @param page the current Playwright page
     * @return true if the recovery condition is detected
     */
    boolean detect(Page page);

    /**
     * The recovery action to suggest when this condition is detected.
     */
    RecoveryAction suggestedAction();

    /**
     * Human-readable description of what this detector looks for.
     */
    String description();

    enum RecoveryAction {
        DISMISS_MODAL,
        RESTORE_SESSION,
        RELOAD_PAGE,
        WAIT_AND_RETRY,
        NAVIGATE_BACK
    }
}
