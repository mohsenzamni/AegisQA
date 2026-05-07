package com.aegisqa.domain.assertion;

import lombok.Builder;
import lombok.Data;

/**
 * Result of a single assertion evaluation.
 */
@Data
@Builder
public class AssertionResult {

    private String assertion;
    private AssertionType type;
    private String expected;
    private String actual;
    private AssertionStatus status;
    private String message;
    private String details;

    public boolean isPassed() {
        return status == AssertionStatus.PASS;
    }

    /**
     * Factory for a passing assertion.
     */
    public static AssertionResult pass(String assertion, String expected, String actual) {
        return AssertionResult.builder()
                .assertion(assertion)
                .expected(expected)
                .actual(actual)
                .status(AssertionStatus.PASS)
                .message("Assertion passed")
                .build();
    }

    /**
     * Factory for a failing assertion.
     */
    public static AssertionResult fail(String assertion, String expected, String actual) {
        return AssertionResult.builder()
                .assertion(assertion)
                .expected(expected)
                .actual(actual)
                .status(AssertionStatus.FAIL)
                .message("Expected [" + expected + "] but got [" + actual + "]")
                .build();
    }
}
