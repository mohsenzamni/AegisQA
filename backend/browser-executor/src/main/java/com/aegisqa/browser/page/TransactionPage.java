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
    private static final String INPUT_AMOUNT = "[name='amount'], [data-testid='amount']";
    private static final String INPUT_CURRENCY = "[name='currency'], [data-testid='currency']";
    private static final String INPUT_CARD_NUMBER = "[name='cardNumber'], [data-testid='card-number']";
    private static final String INPUT_EXPIRY = "[name='expiry'], [name='expiryDate'], [data-testid='expiry']";
    private static final String INPUT_CVV = "[name='cvv'], [name='cvc'], [data-testid='cvv']";
    private static final String INPUT_PURCHASE_DATE = "[name='purchaseDate'], [data-testid='purchase-date']";
    private static final String INPUT_CARDHOLDER_NAME = "[name='cardholderName'], [data-testid='cardholder-name']";
    private static final String INPUT_EMAIL = "[name='email'], [data-testid='email']";
    private static final String INPUT_MERCHANT_CATEGORY = "[name='merchantCategoryCode'], [data-testid='mcc']";
    private static final String INPUT_CHALLENGE_IND = "[name='challengeIndicator'], [data-testid='challenge-ind']";
    private static final String RESULT_STATUS = "[data-testid='transaction-status'], .transaction-status";
    private static final String WHITELIST_CHECKBOX = "[name='whitelist'], [data-testid='whitelist-checkbox']";
    private static final String WHITELIST_STATUS = "[data-testid='whitelist-status'], .whitelist-status";
    private final String brwUrl;

    public TransactionPage(Page page, String brwUrl) {
        super(page);
        this.brwUrl = brwUrl;
    }

    public void openTransaction() {
        log.info("Opening transaction page: {}", brwUrl);
        page.navigate(brwUrl);
        waitForIdle();
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

    public void setAmount(String amount) {
        fill(INPUT_AMOUNT, amount);
    }

    public void setCurrency(String currency) {
        fill(INPUT_CURRENCY, currency);
    }

    public void setCardNumber(String cardNumber) {
        fill(INPUT_CARD_NUMBER, cardNumber);
    }

    public void setExpiry(String expiry) {
        fill(INPUT_EXPIRY, expiry);
    }

    public void setCvv(String cvv) {
        fill(INPUT_CVV, cvv);
    }

    public void setPurchaseDate(String purchaseDate) {
        fill(INPUT_PURCHASE_DATE, purchaseDate);
    }

    public void setCardholderName(String cardholderName) {
        fill(INPUT_CARDHOLDER_NAME, cardholderName);
    }

    public void setEmail(String email) {
        fill(INPUT_EMAIL, email);
    }

    public void setMerchantCategoryCode(String merchantCategoryCode) {
        fill(INPUT_MERCHANT_CATEGORY, merchantCategoryCode);
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
