package com.aegisqa.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ACS Transaction Status codes as defined by EMVCo 3DS specification.
 */
@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    Y("Authenticated / Frictionless", "Authentication successful, no challenge required"),
    C("Challenge Required", "Additional authentication required via challenge"),
    N("Not Authenticated", "Authentication failed"),
    U("Authentication Could Not Be Performed", "Technical or other issue prevented authentication"),
    R("Authentication Rejected", "Issuer rejected authentication"),
    A("Attempts Processing Performed", "Not authenticated but proof of attempt generated");

    private final String displayName;
    private final String description;

    public boolean isSuccessful() {
        return this == Y || this == A;
    }

    public boolean requiresChallenge() {
        return this == C;
    }

    public boolean isFailed() {
        return this == N || this == R;
    }

    public boolean isUnavailable() {
        return this == U;
    }
}
