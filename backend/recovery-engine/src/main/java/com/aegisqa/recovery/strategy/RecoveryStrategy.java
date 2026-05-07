package com.aegisqa.recovery.strategy;

import com.aegisqa.recovery.detector.RecoveryDetector;
import com.microsoft.playwright.Page;

/**
 * Strategy for executing a recovery action on a page.
 */
public interface RecoveryStrategy {

    /**
     * The recovery action this strategy handles.
     */
    RecoveryDetector.RecoveryAction handles();

    /**
     * Perform the recovery.
     *
     * @param page        the current page
     * @param executionId the execution ID for logging
     */
    void recover(Page page, String executionId);
}
