package com.aegisqa.orchestration.event;

import java.time.Instant;

public record ExecutionCompletedEvent(
        String executionId,
        int totalSteps,
        int passedSteps,
        int failedSteps,
        long durationMs,
        Instant timestamp
) implements ExecutionEvent {}
