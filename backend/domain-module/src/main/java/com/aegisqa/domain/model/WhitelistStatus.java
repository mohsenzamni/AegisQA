package com.aegisqa.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Whitelist status values per EMVCo 3DS spec.
 */
@Getter
@RequiredArgsConstructor
public enum WhitelistStatus {
    Y("Whitelisted", "Merchant added to whitelist"),
    N("Not Whitelisted", "Merchant not on whitelist"),
    E("Whitelist Pending", "Whitelist action pending"),
    R("Whitelist Rejected", "Whitelist request rejected"),
    P("Whitelist Processing", "Whitelist addition in progress"),
    U("Whitelist Unknown", "Whitelist status unknown");

    private final String displayName;
    private final String description;
}
