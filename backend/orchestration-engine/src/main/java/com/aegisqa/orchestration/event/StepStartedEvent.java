package com.aegisqa.orchestration.event;

import java.time.Instant;

public record StepStartedEvent(
        String executionId,
        int stepNumber,
        String actionType,
        Instant timestamp
) implements ExecutionEvent {}
