package com.aegisqa.browser.page;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * Page Object for the ACS Risk Management module.
 * Encapsulates interactions with RBA adapter creation, risk chain setup, and scoring configuration.
 */
@Slf4j
public class RiskManagementPage extends PageObject {

    private static final String MENU_RISK_MANAGEMENT = "text=Risk Management";
    private static final String BTN_ADD_RBA_ADAPTER = "text=Add RBA Adapter";
    private static final String BTN_CREATE_RISK_CHAIN = "text=Create Risk Chain";
    private static final String INPUT_CHAIN_NAME = "[name='chainName'], [placeholder*='chain name']";
    private static final String INPUT_SCORE_MIN = "[name='scoreMin'], [data-testid='score-min']";
    private static final String INPUT_SCORE_MAX = "[name='scoreMax'], [data-testid='score-max']";
    private static final String BTN_SAVE = "button:has-text('Save'), button:has-text('Submit')";

    public RiskManagementPage(Page page) {
        super(page);
    }

    public void openRiskManagement() {
        log.info("Opening Risk Management module");
        click(MENU_RISK_MANAGEMENT);
        waitForIdle();
    }

    public void addRbaAdapter() {
        log.info("Adding RBA adapter");
        click(BTN_ADD_RBA_ADAPTER);
        waitForIdle();
        screenshot("add_rba_adapter");
    }

    public void createRiskChain(String chainName) {
        log.info("Creating risk chain: {}", chainName);
        click(BTN_CREATE_RISK_CHAIN);
        if (chainName != null && !chainName.isBlank()) {
            fill(INPUT_CHAIN_NAME, chainName);
        }
        screenshot("create_risk_chain");
    }

    public void setFrictionlessScore(int min, int max) {
        log.info("Setting frictionless score range: {}..{}", min, max);
        fill(INPUT_SCORE_MIN, String.valueOf(min));
        fill(INPUT_SCORE_MAX, String.valueOf(max));
        click(BTN_SAVE);
        waitForIdle();
        screenshot("set_frictionless_score");
    }

    public void save() {
        click(BTN_SAVE);
        waitForIdle();
    }
}
