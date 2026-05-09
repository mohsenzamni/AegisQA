package com.aegisqa.domain.execution;

import com.aegisqa.domain.assertion.AssertionResult;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.time.Instant;
import java.util.List;

/**
 * Result of executing a single scenario step.
 */
@Data
@Builder
public class StepResult {

    private int stepNumber;
    private String actionType;
    private ExecutionStatus status;

    private Instant startedAt;
    private Instant completedAt;
    private long durationMs;

    private String screenshotPath;
    private String errorMessage;
    private String errorDetails;

    @Singular
    private List<AssertionResult> assertionResults;

    public boolean isPassed() {
        return status == ExecutionStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == ExecutionStatus.FAILED;
    }
}
