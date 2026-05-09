package com.aegisqa.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationOutcome {
    FRICTIONLESS_SUCCESS("Frictionless authentication succeeded"),
    FRICTIONLESS_FAILURE("Frictionless authentication failed"),
    CHALLENGE_REQUIRED("Challenge step-up required"),
    CHALLENGE_SUCCESS("Challenge authentication succeeded"),
    CHALLENGE_FAILURE("Challenge authentication failed"),
    DECOUPLED_REQUIRED("Decoupled authentication required"),
    TECHNICAL_FAILURE("Technical failure during authentication"),
    UNAVAILABLE("Authentication service unavailable");

    private final String description;
}
