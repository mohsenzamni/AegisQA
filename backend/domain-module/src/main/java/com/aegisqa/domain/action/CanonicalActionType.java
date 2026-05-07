package com.aegisqa.domain.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * All supported canonical action types in the AegisQA platform.
 */
@Getter
@RequiredArgsConstructor
public enum CanonicalActionType {
    // ACS / Risk Management
    ADD_RBA_ADAPTER("add_rba_adapter", "Add RBA adapter to ACS"),
    CREATE_RISK_CHAIN("create_risk_chain", "Create a risk evaluation chain"),
    SET_RISK_SCORE("set_risk_score", "Set frictionless score range"),
    CONFIGURE_WHITELIST("configure_whitelist", "Configure whitelist settings"),
    ENABLE_WHITELIST("enable_whitelist", "Enable whitelist on BIN"),

    // Transaction execution
    RUN_TRANSACTION("run_transaction", "Execute a payment scheme transaction"),
    RUN_EMV_TRANSACTION("run_emv_transaction", "Execute an EMV 3DS transaction"),
    RUN_VISA_TRANSACTION("run_visa_transaction", "Execute a Visa transaction"),
    RUN_MASTERCARD_TRANSACTION("run_mastercard_transaction", "Execute a Mastercard transaction"),

    // Navigation
    NAVIGATE_TO("navigate_to", "Navigate to a URL or page"),
    OPEN_MODULE("open_module", "Open an ACS module"),

    // Assertions
    VERIFY_TRANSACTION_STATUS("verify_transaction_status", "Verify transaction authentication status"),
    VERIFY_ARES("verify_ares", "Verify ARes message field"),
    VERIFY_RREQ("verify_rreq", "Verify RReq message field"),
    VERIFY_WHITELIST_STATUS("verify_whitelist_status", "Verify whitelist status"),
    ASSERT_FIELD("assert_field", "Assert a specific field value"),
    ASSERT_UI_TEXT("assert_ui_text", "Assert text visible in UI"),

    // Browser operations
    CLICK("click", "Click a UI element"),
    FILL("fill", "Fill a form field"),
    WAIT_FOR("wait_for", "Wait for UI condition"),
    TAKE_SCREENSHOT("take_screenshot", "Capture a screenshot"),

    // Session management
    LOGIN("login", "Authenticate into the ACS system"),
    LOGOUT("logout", "Log out of the ACS system"),

    // Custom
    CUSTOM("custom", "Custom action with raw parameters");

    private final String code;
    private final String description;

    public static CanonicalActionType fromCode(String code) {
        for (CanonicalActionType t : values()) {
            if (t.code.equalsIgnoreCase(code)) {
                return t;
            }
        }
        return CUSTOM;
    }
}
