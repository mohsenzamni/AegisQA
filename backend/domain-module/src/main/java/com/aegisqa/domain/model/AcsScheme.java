package com.aegisqa.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AcsScheme {
    VISA("Visa", "0002"),
    MASTERCARD("Mastercard", "0001"),
    AMEX("American Express", "0003"),
    DISCOVER("Discover", "0004"),
    JCB("JCB", "0005");

    private final String displayName;
    private final String directoryServerId;
}
