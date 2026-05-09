package com.aegisqa.orchestration.event;

import java.time.Instant;

public record ExecutionFailedEvent(
        String executionId,
        int failedAtStep,
        String errorMessage,
        Instant timestamp
) implements ExecutionEvent {}
