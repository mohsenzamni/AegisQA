package com.aegisqa.recovery.service;

import com.aegisqa.recovery.detector.RecoveryDetector;
import com.aegisqa.recovery.strategy.RecoveryStrategy;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Recovery service that runs all detectors and applies appropriate recovery strategies.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecoveryService {

    private final List<RecoveryDetector> detectors;
    private final List<RecoveryStrategy> strategies;

    /**
     * Scan the page for recovery conditions and apply the first matching strategy.
     *
     * @param page        current Playwright page
     * @param executionId execution ID for logging
     * @return true if a recovery was applied
     */
    public boolean attemptRecovery(Page page, String executionId) {
        Map<RecoveryDetector.RecoveryAction, RecoveryStrategy> strategyMap = strategies.stream()
                .collect(Collectors.toMap(RecoveryStrategy::handles, Function.identity(),
                        (a, b) -> a));

        for (RecoveryDetector detector : detectors) {
            if (detector.detect(page)) {
                log.info("[{}] Recovery condition detected: {}", executionId, detector.description());
                RecoveryDetector.RecoveryAction action = detector.suggestedAction();
                RecoveryStrategy strategy = strategyMap.get(action);
                if (strategy != null) {
                    strategy.recover(page, executionId);
                    return true;
                }
                log.warn("[{}] No strategy for recovery action: {}", executionId, action);
            }
        }
        return false;
    }
}
