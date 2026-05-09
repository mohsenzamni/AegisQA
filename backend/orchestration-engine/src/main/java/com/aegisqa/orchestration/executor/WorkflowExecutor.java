package com.aegisqa.orchestration.executor;

import com.aegisqa.domain.execution.ExecutionState;
import com.aegisqa.domain.scenario.Scenario;

/**
 * Executes a complete scenario workflow step by step.
 */
public interface WorkflowExecutor {

    /**
     * Execute the given scenario synchronously.
     *
     * @param scenario  the parsed scenario to execute
     * @param executionId a unique ID for this execution run
     * @return final execution state after completion
     */
    ExecutionState execute(Scenario scenario, String executionId);

    /**
     * Retry a specific failed step.
     */
    void retryStep(String executionId, int stepNumber);

    /**
     * Cancel a running execution.
     */
    void cancel(String executionId);
}
