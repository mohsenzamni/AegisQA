package com.aegisqa.domain.assertion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssertionType {
    UI_STATE("UI state assertion"),
    API_RESPONSE("API response assertion"),
    DB_STATE("Database state assertion"),
    TRANSACTION_STATUS("ACS transaction status assertion"),
    FIELD_VALUE("Field value assertion"),
    TEXT_PRESENT("Text presence assertion"),
    CUSTOM("Custom assertion");

    private final String description;
}
