package com.aegisqa.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ACS Challenge result codes.
 */
@Getter
@RequiredArgsConstructor
public enum ChallengeResult {
    AUTHENTICATED("Y", "Challenge completed successfully"),
    NOT_AUTHENTICATED("N", "Challenge failed"),
    CANCELLED("U", "Challenge cancelled by cardholder"),
    TIMED_OUT("T", "Challenge timed out");

    private final String code;
    private final String description;
}
