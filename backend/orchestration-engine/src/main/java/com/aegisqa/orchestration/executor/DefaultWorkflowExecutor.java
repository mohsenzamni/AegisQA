package com.aegisqa.orchestration.executor;

import com.aegisqa.domain.execution.ExecutionState;
import com.aegisqa.domain.execution.ExecutionStatus;
import com.aegisqa.domain.execution.StepResult;
import com.aegisqa.domain.scenario.Scenario;
import com.aegisqa.domain.scenario.ScenarioStep;
import com.aegisqa.orchestration.event.ExecutionCompletedEvent;
import com.aegisqa.orchestration.event.ExecutionFailedEvent;
import com.aegisqa.orchestration.event.ExecutionStartedEvent;
import com.aegisqa.orchestration.event.StepCompletedEvent;
import com.aegisqa.orchestration.event.StepFailedEvent;
import com.aegisqa.orchestration.event.StepStartedEvent;
import com.aegisqa.orchestration.retry.RetryExecutor;
import com.aegisqa.orchestration.retry.RetryPolicy;
import com.aegisqa.orchestration.state.ExecutionStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default workflow executor that iterates scenario steps, applies retry policies,
 * and publishes lifecycle events. Browser and ACS tool execution is delegated via
 * a StepExecutionDispatcher (injected separately to keep orchestration decoupled).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultWorkflowExecutor implements WorkflowExecutor {

    private final ExecutionStateManager stateManager;
    private final RetryExecutor retryExecutor;
    private final ApplicationEventPublisher eventPublisher;
    private final StepExecutionDispatcher stepDispatcher;

    @Override
    public ExecutionState execute(Scenario scenario, String executionId) {
        log.info("Starting execution {} for scenario '{}'", executionId, scenario.getName());
        Instant start = Instant.now();

        ExecutionState state = stateManager.create(executionId, scenario.getId(), scenario.getSteps().size());
        stateManager.updateStatus(executionId, ExecutionStatus.RUNNING);
        eventPublisher.publishEvent(new ExecutionStartedEvent(executionId, scenario.getId(),
                scenario.getSteps().size(), Instant.now()));

        int passed = 0;
        int failed = 0;

        for (ScenarioStep step : scenario.getSteps()) {
            if (isCancelled(executionId)) {
                log.info("Execution {} cancelled at step {}", executionId, step.getStepNumber());
                break;
            }

            stateManager.advanceStep(executionId);
            eventPublisher.publishEvent(new StepStartedEvent(executionId, step.getStepNumber(),
                    step.getActionType(), Instant.now()));

            StepResult result = executeStepWithRetry(executionId, step);
            state.addStepResult(result);

            if (result.isPassed()) {
                passed++;
                eventPublisher.publishEvent(new StepCompletedEvent(executionId,
                        step.getStepNumber(), result, Instant.now()));
            } else {
                failed++;
                eventPublisher.publishEvent(new StepFailedEvent(executionId, step.getStepNumber(),
                        step.getActionType(), result.getErrorMessage(), 1, Instant.now()));
                log.warn("Step {} failed: {}", step.getStepNumber(), result.getErrorMessage());
                // Continue to next step (soft failure). For hard stops, add break here.
            }
        }

        long durationMs = Instant.now().toEpochMilli() - start.toEpochMilli();
        ExecutionStatus finalStatus = failed == 0 ? ExecutionStatus.COMPLETED : ExecutionStatus.FAILED;
        stateManager.updateStatus(executionId, finalStatus);

        if (finalStatus == ExecutionStatus.COMPLETED) {
            eventPublisher.publishEvent(new ExecutionCompletedEvent(executionId,
                    scenario.getSteps().size(), passed, failed, durationMs, Instant.now()));
        } else {
            eventPublisher.publishEvent(new ExecutionFailedEvent(executionId, state.getCurrentStep(),
                    "One or more steps failed", Instant.now()));
        }

        log.info("Execution {} finished: status={}, passed={}, failed={}, duration={}ms",
                executionId, finalStatus, passed, failed, durationMs);

        return stateManager.get(executionId).orElse(state);
    }

    @Override
    public void retryStep(String executionId, int stepNumber) {
        log.info("Retrying step {} in execution {}", stepNumber, executionId);
        stateManager.updateStatus(executionId, ExecutionStatus.RETRYING);
        // Actual retry logic delegated to stepDispatcher
        stateManager.updateStatus(executionId, ExecutionStatus.RUNNING);
    }

    @Override
    public void cancel(String executionId) {
        log.info("Cancelling execution {}", executionId);
        stateManager.updateStatus(executionId, ExecutionStatus.CANCELLED);
    }

    private StepResult executeStepWithRetry(String executionId, ScenarioStep step) {
        RetryPolicy policy = RetryPolicy.defaultPolicy();
        Instant stepStart = Instant.now();
        try {
            return retryExecutor.execute(
                    () -> stepDispatcher.dispatch(executionId, step),
                    policy,
                    "step-" + step.getStepNumber() + ":" + step.getActionType()
            );
        } catch (Exception e) {
            long duration = Instant.now().toEpochMilli() - stepStart.toEpochMilli();
            log.error("Step {} permanently failed after retries: {}", step.getStepNumber(), e.getMessage());
            return StepResult.builder()
                    .stepNumber(step.getStepNumber())
                    .actionType(step.getActionType())
                    .status(ExecutionStatus.FAILED)
                    .startedAt(stepStart)
                    .completedAt(Instant.now())
                    .durationMs(duration)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    private boolean isCancelled(String executionId) {
        return stateManager.get(executionId)
                .map(s -> s.getStatus() == ExecutionStatus.CANCELLED)
                .orElse(false);
    }
}
