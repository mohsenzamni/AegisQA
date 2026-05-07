package com.aegisqa.orchestration.executor;

import com.aegisqa.domain.execution.ExecutionStatus;
import com.aegisqa.domain.execution.StepResult;
import com.aegisqa.domain.scenario.ScenarioStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Dispatches individual scenario steps to the appropriate executor.
 * In Phase 1 this is a stub that logs and succeeds.
 * In Phase 2+ it delegates to BrowserExecutorService, API clients, etc.
 */
@Slf4j
@Component
public class StepExecutionDispatcher {

    public StepResult dispatch(String executionId, ScenarioStep step) {
        Instant start = Instant.now();
        log.info("[{}] Executing step {}: {} params={}", executionId,
                step.getStepNumber(), step.getActionType(), step.getParameters());

        // Phase 1: deterministic stub execution
        // In Phase 2, delegate to BrowserExecutorService or ACS API clients
        simulateExecution(step.getActionType());

        long duration = Instant.now().toEpochMilli() - start.toEpochMilli();
        return StepResult.builder()
                .stepNumber(step.getStepNumber())
                .actionType(step.getActionType())
                .status(ExecutionStatus.COMPLETED)
                .startedAt(start)
                .completedAt(Instant.now())
                .durationMs(duration)
                .build();
    }

    private void simulateExecution(String actionType) {
        // Structured log for observability
        log.debug("Dispatching canonical action: {}", actionType);
    }
}
