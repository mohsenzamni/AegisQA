package com.aegisqa.browser.page;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * Page Object for issuer/admin settings before transaction execution.
 */
@Slf4j
public class IssuerSettingsPage extends PageObject {

    private static final String INPUT_BIN_RANGE = "[name='binRange'], [data-testid='bin-range']";
    private static final String SELECT_EMV_VERSION = "[name='defaultEmvVersion'], [data-testid='default-emv-version']";
    private static final String SELECT_CHALLENGE_PREF = "[name='challengePreference'], [data-testid='challenge-preference']";
    private static final String TOGGLE_PURCHASE_DATE_VALIDATION = "[name='purchaseDateValidation'], [data-testid='purchase-date-validation']";
    private static final String BTN_SAVE = "button:has-text('Save'), button:has-text('Submit')";

    private final String adminBaseUrl;

    public IssuerSettingsPage(Page page, String adminBaseUrl) {
        super(page);
        this.adminBaseUrl = adminBaseUrl;
    }

    public void openIssuerSettings() {
        String base = adminBaseUrl.endsWith("/") ? adminBaseUrl : adminBaseUrl + "/";
        String issuerSettingsUrl = base + "issuer/settings";
        log.info("Opening issuer settings: {}", issuerSettingsUrl);
        page.navigate(issuerSettingsUrl);
        waitForIdle();
    }

    public void configureIssuerSettings(String binRange, String defaultEmvVersion, String challengePreference) {
        if (binRange != null && !binRange.isBlank()) {
            fill(INPUT_BIN_RANGE, binRange);
        }
        if (defaultEmvVersion != null && !defaultEmvVersion.isBlank()) {
            page.selectOption(SELECT_EMV_VERSION, defaultEmvVersion);
        }
        if (challengePreference != null && !challengePreference.isBlank()) {
            page.selectOption(SELECT_CHALLENGE_PREF, challengePreference);
        }
        save();
        screenshot("configure_issuer_settings");
    }

    public void disablePurchaseDateValidation() {
        log.info("Disabling purchase date validation");
        if (page.locator(TOGGLE_PURCHASE_DATE_VALIDATION).isChecked()) {
            page.uncheck(TOGGLE_PURCHASE_DATE_VALIDATION);
        }
        save();
        screenshot("disable_purchase_date_validation");
    }

    public void configureBinRange(String binRange) {
        log.info("Configuring BIN range: {}", binRange);
        fill(INPUT_BIN_RANGE, binRange);
        save();
        screenshot("configure_bin_range");
    }

    public void setDefaultEmvVersion(String version) {
        log.info("Setting default EMV version: {}", version);
        page.selectOption(SELECT_EMV_VERSION, version);
        save();
        screenshot("set_default_emv_version");
    }

    public void setChallengePreference(String preference) {
        log.info("Setting challenge preference: {}", preference);
        page.selectOption(SELECT_CHALLENGE_PREF, preference);
        save();
        screenshot("set_challenge_preference");
    }

    private void save() {
        click(BTN_SAVE);
        waitForIdle();
    }
}
