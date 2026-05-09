package com.aegisqa.orchestration.event;

import java.time.Instant;

public record ExecutionStartedEvent(
        String executionId,
        String scenarioId,
        int totalSteps,
        Instant timestamp
) implements ExecutionEvent {}
