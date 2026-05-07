package com.aegisqa.gateway.dto;

import com.aegisqa.domain.execution.ExecutionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response DTO for execution status queries.
 */
@Data
@Builder
public class ExecutionResponse {

    private String executionId;
    private String scenarioId;
    private ExecutionStatus status;
    private int currentStep;
    private int totalSteps;
    private String transactionId;
    private Instant startedAt;
    private Instant completedAt;
    private String lastError;
    private int retryCount;
}
