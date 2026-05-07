package com.aegisqa.orchestration.event;

import java.time.Instant;

public record StepFailedEvent(
        String executionId,
        int stepNumber,
        String actionType,
        String errorMessage,
        int attemptNumber,
        Instant timestamp
) implements ExecutionEvent {}
