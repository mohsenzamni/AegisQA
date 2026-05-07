package com.aegisqa.orchestration.event;

import com.aegisqa.domain.execution.ExecutionState;
import com.aegisqa.domain.execution.StepResult;

import java.time.Instant;

/**
 * Sealed hierarchy of execution lifecycle events emitted by the orchestration engine.
 */
public sealed interface ExecutionEvent permits
        ExecutionStartedEvent,
        StepStartedEvent,
        StepCompletedEvent,
        StepFailedEvent,
        ExecutionCompletedEvent,
        ExecutionFailedEvent {

    String executionId();
    Instant timestamp();
}
