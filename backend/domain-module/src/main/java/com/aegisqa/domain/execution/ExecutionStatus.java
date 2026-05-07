package com.aegisqa.domain.execution;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExecutionStatus {
    PENDING("Pending", false),
    RUNNING("Running", false),
    PAUSED("Paused", false),
    COMPLETED("Completed", true),
    FAILED("Failed", true),
    RETRYING("Retrying", false),
    CANCELLED("Cancelled", true);

    private final String displayName;
    private final boolean terminal;
}
