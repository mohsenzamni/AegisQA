package com.aegisqa.domain.assertion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssertionStatus {
    PASS("PASS"),
    FAIL("FAIL"),
    SKIPPED("SKIPPED"),
    PENDING("PENDING"),
    ERROR("ERROR");

    private final String code;
}
