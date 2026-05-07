package com.aegisqa.reporting.model;

import com.aegisqa.domain.assertion.AssertionResult;
import com.aegisqa.domain.execution.ExecutionState;
import com.aegisqa.domain.execution.ExecutionStatus;
import com.aegisqa.domain.execution.StepResult;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.time.Instant;
import java.util.List;

/**
 * Full execution report combining state, step results, assertions, and AI summaries.
 */
@Data
@Builder
public class ExecutionReport {

    private String reportId;
    private String executionId;
    private String scenarioId;
    private String scenarioName;
    private ExecutionStatus finalStatus;

    private Instant startedAt;
    private Instant completedAt;
    private long durationMs;

    private int totalSteps;
    private int passedSteps;
    private int failedSteps;

    @Singular
    private List<StepResult> stepResults;

    @Singular
    private List<AssertionResult> allAssertions;

    private String aiSummary;
    private String htmlReportPath;
    private String jsonReportPath;

    private String screenshotsDir;
    private String videosDir;
    private String harDir;

    public double successRate() {
        if (totalSteps == 0) return 0.0;
        return (double) passedSteps / totalSteps * 100;
    }
}
