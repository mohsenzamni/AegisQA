package com.aegisqa.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmvVersion {
    V_2_1("2.1", "EMV 3DS 2.1"),
    V_2_2("2.2", "EMV 3DS 2.2"),
    V_2_3("2.3", "EMV 3DS 2.3");

    private final String version;
    private final String displayName;

    public static EmvVersion fromString(String version) {
        for (EmvVersion v : values()) {
            if (v.version.equals(version)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Unknown EMV version: " + version);
    }
}
