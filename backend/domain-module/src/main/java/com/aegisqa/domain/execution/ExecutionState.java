package com.aegisqa.domain.execution;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    // ACS-specific state
    private String transactionId;
    private String riskChainId;
    private String browserSessionId;

    // Timing
    private Instant startedAt;
    private Instant completedAt;

    // Step results
    @Builder.Default
    private List<StepResult> stepResults = new ArrayList<>();

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
}
