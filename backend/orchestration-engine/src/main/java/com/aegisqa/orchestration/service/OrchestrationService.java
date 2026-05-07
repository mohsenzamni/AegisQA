package com.aegisqa.orchestration.service;

import com.aegisqa.domain.execution.ExecutionState;
import com.aegisqa.domain.scenario.Scenario;
import com.aegisqa.orchestration.executor.WorkflowExecutor;
import com.aegisqa.orchestration.state.ExecutionStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * High-level orchestration service used by the API gateway.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestrationService {

    private final WorkflowExecutor workflowExecutor;
    private final ExecutionStateManager stateManager;

    /**
     * Start executing a scenario, returning the execution ID immediately.
     */
    public String startExecution(Scenario scenario) {
        String executionId = UUID.randomUUID().toString();
        log.info("Scheduling execution {} for scenario '{}'", executionId, scenario.getName());
        // Execute synchronously for now; wrap in CompletableFuture for async
        workflowExecutor.execute(scenario, executionId);
        return executionId;
    }

    /**
     * Get current execution state.
     */
    public Optional<ExecutionState> getState(String executionId) {
        return stateManager.get(executionId);
    }

    /**
     * Cancel a running execution.
     */
    public void cancel(String executionId) {
        workflowExecutor.cancel(executionId);
    }

    /**
     * Retry a specific failed step.
     */
    public void retryStep(String executionId, int stepNumber) {
        workflowExecutor.retryStep(executionId, stepNumber);
    }
}
