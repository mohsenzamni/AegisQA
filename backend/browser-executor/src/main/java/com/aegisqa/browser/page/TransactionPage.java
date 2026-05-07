package com.aegisqa.browser.page;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * Page Object for ACS transaction execution and result verification.
 */
@Slf4j
public class TransactionPage extends PageObject {

    private static final String BTN_RUN_TRANSACTION = "button:has-text('Run Transaction'), button:has-text('Execute')";
    private static final String SELECT_SCHEME = "[name='scheme'], [data-testid='scheme-select']";
    private static final String SELECT_EMV_VERSION = "[name='emvVersion'], [data-testid='emv-version']";
    private static final String INPUT_CHALLENGE_IND = "[name='challengeIndicator'], [data-testid='challenge-ind']";
    private static final String RESULT_STATUS = "[data-testid='transaction-status'], .transaction-status";
    private static final String WHITELIST_CHECKBOX = "[name='whitelist'], [data-testid='whitelist-checkbox']";
    private static final String WHITELIST_STATUS = "[data-testid='whitelist-status'], .whitelist-status";

    public TransactionPage(Page page) {
        super(page);
    }

    public void selectScheme(String scheme) {
        log.info("Selecting payment scheme: {}", scheme);
        page.selectOption(SELECT_SCHEME, scheme.toLowerCase());
    }

    public void selectEmvVersion(String version) {
        log.info("Selecting EMV version: {}", version);
        page.selectOption(SELECT_EMV_VERSION, version);
    }

    public void setChallengeIndicator(String indicator) {
        fill(INPUT_CHALLENGE_IND, indicator);
    }

    public void runTransaction() {
        log.info("Running transaction");
        click(BTN_RUN_TRANSACTION);
        waitForIdle();
        screenshot("transaction_result");
    }

    public String getTransactionStatus() {
        waitForText("Transaction");
        return getText(RESULT_STATUS).trim();
    }

    public void checkWhitelistCheckbox() {
        log.info("Checking whitelist checkbox");
        page.check(WHITELIST_CHECKBOX);
    }

    public String getWhitelistStatus() {
        return getText(WHITELIST_STATUS).trim();
    }
}
