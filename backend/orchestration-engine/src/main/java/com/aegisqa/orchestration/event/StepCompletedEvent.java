package com.aegisqa.orchestration.event;

import com.aegisqa.domain.execution.StepResult;

import java.time.Instant;

public record StepCompletedEvent(
        String executionId,
        int stepNumber,
        StepResult result,
        Instant timestamp
) implements ExecutionEvent {}
