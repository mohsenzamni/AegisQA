package com.aegisqa.orchestration.state;

import com.aegisqa.domain.execution.ExecutionState;
import com.aegisqa.domain.execution.ExecutionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of ExecutionStateManager.
 * For production, replace with a persistent store (Redis or PostgreSQL).
 */
@Slf4j
@Component
public class InMemoryExecutionStateManager implements ExecutionStateManager {

    private final Map<String, ExecutionState> store = new ConcurrentHashMap<>();

    @Override
    public ExecutionState create(String executionId, String scenarioId, int totalSteps) {
        ExecutionState state = ExecutionState.builder()
                .executionId(executionId)
                .scenarioId(scenarioId)
                .status(ExecutionStatus.PENDING)
                .currentStep(0)
                .totalSteps(totalSteps)
                .startedAt(Instant.now())
                .build();
        store.put(executionId, state);
        log.debug("Created execution state: {}", executionId);
        return state;
    }

    @Override
    public Optional<ExecutionState> get(String executionId) {
        return Optional.ofNullable(store.get(executionId));
    }

    @Override
    public void updateStatus(String executionId, ExecutionStatus status) {
        store.computeIfPresent(executionId, (id, state) -> {
            state.setStatus(status);
            if (status.isTerminal()) {
                state.setCompletedAt(Instant.now());
            }
            return state;
        });
    }

    @Override
    public void advanceStep(String executionId) {
        store.computeIfPresent(executionId, (id, state) -> {
            state.setCurrentStep(state.getCurrentStep() + 1);
            return state;
        });
    }

    @Override
    public void setContextValue(String executionId, String key, String value) {
        store.computeIfPresent(executionId, (id, state) -> {
            state.setContextValue(key, value);
            return state;
        });
    }

    @Override
    public void remove(String executionId) {
        store.remove(executionId);
        log.debug("Removed execution state: {}", executionId);
    }
}
