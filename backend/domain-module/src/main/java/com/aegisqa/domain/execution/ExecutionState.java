package com.aegisqa.domain.execution;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Live execution state for a running scenario.
 */
@Data
@Builder
public class ExecutionState {

    private String executionId;
    private String scenarioId;
    private ExecutionStatus status;
    private int currentStep;
    private int totalSteps;

    // Well-known ACS-specific state fields (also stored in context for easy access)
    private String transactionId;
    private String riskChainId;
    private String browserSessionId;

    // Timing
    private Instant startedAt;
    private Instant completedAt;

    // Step results
    @Builder.Default
    private List<StepResult> stepResults = new ArrayList<>();

    // Arbitrary context values (supports extensions beyond the well-known fields)
    @Builder.Default
    private Map<String, String> context = new HashMap<>();

    // Error tracking
    private String lastError;
    private int retryCount;

    public boolean isRunning() {
        return status == ExecutionStatus.RUNNING || status == ExecutionStatus.RETRYING;
    }

    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }

    public void addStepResult(StepResult result) {
        stepResults.add(result);
    }

    /**
     * Store an arbitrary context value.
     * Also updates well-known fields (transactionId, riskChainId, browserSessionId)
     * for backwards compatibility and convenience.
     */
    public void setContextValue(String key, String value) {
        context.put(key, value);
        switch (key) {
            case "transactionId" -> transactionId = value;
            case "riskChainId" -> riskChainId = value;
            case "browserSessionId" -> browserSessionId = value;
            default -> { /* stored only in context map */ }
        }
    }

    public String getContextValue(String key) {
        return context.get(key);
    }
}
