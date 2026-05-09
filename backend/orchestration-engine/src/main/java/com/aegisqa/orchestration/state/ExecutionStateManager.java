package com.aegisqa.orchestration.state;

import com.aegisqa.domain.execution.ExecutionState;
import com.aegisqa.domain.execution.ExecutionStatus;

import java.util.Optional;

/**
 * Manages the lifecycle state of scenario executions.
 */
public interface ExecutionStateManager {

    /**
     * Create and store a new execution state.
     */
    ExecutionState create(String executionId, String scenarioId, int totalSteps);

    /**
     * Retrieve execution state by ID.
     */
    Optional<ExecutionState> get(String executionId);

    /**
     * Update the execution status.
     */
    void updateStatus(String executionId, ExecutionStatus status);

    /**
     * Advance the current step counter.
     */
    void advanceStep(String executionId);

    /**
     * Store a key-value in execution context (e.g. transactionId, riskChainId).
     */
    void setContextValue(String executionId, String key, String value);

    /**
     * Delete a completed/failed execution state.
     */
    void remove(String executionId);
}
