package com.aegisqa.orchestration.executor;

import com.aegisqa.browser.page.IssuerSettingsPage;
import com.aegisqa.browser.page.RiskManagementPage;
import com.aegisqa.browser.page.TransactionPage;
import com.aegisqa.browser.service.BrowserExecutorService;
import com.aegisqa.domain.action.CanonicalActionType;
import com.aegisqa.domain.execution.ExecutionStatus;
import com.aegisqa.domain.execution.StepResult;
import com.aegisqa.domain.scenario.ScenarioStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * Dispatches individual scenario steps to the appropriate executor.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StepExecutionDispatcher {

    private final BrowserExecutorService browserExecutorService;

    @Value("${aegisqa.acs.admin-url}")
    private String adminUrl;

    @Value("${aegisqa.acs.brw-url}")
    private String brwUrl;

    public StepResult dispatch(String executionId, ScenarioStep step) {
        Instant start = Instant.now();
        log.info("[{}] Executing step {}: {} params={}", executionId,
                step.getStepNumber(), step.getActionType(), step.getParameters());

        executeAction(executionId, step);

        long duration = Instant.now().toEpochMilli() - start.toEpochMilli();
        return StepResult.builder()
                .stepNumber(step.getStepNumber())
                .actionType(step.getActionType())
                .status(ExecutionStatus.COMPLETED)
                .startedAt(start)
                .completedAt(Instant.now())
                .durationMs(duration)
                .build();
    }

    private void executeAction(String executionId, ScenarioStep step) {
        Map<String, Object> params = step.getParameters() == null ? Collections.emptyMap() : step.getParameters();
        CanonicalActionType actionType = CanonicalActionType.fromCode(step.getActionType());

        switch (actionType) {
            case NAVIGATE_TO -> browserExecutorService.navigate(executionId, requiredString(params, "url"));
            case CLICK -> browserExecutorService.click(executionId, requiredString(params, "selector"));
            case FILL -> browserExecutorService.fill(executionId,
                    requiredString(params, "selector"), requiredString(params, "value"));
            case WAIT_FOR -> {
                if (params.containsKey("text")) {
                    browserExecutorService.getPage(executionId).waitForSelector("text=" + params.get("text"));
                } else if (params.containsKey("selector")) {
                    browserExecutorService.getPage(executionId).waitForSelector(params.get("selector").toString());
                } else if (params.containsKey("ms")) {
                    browserExecutorService.getPage(executionId).waitForTimeout(Long.parseLong(params.get("ms").toString()));
                } else {
                    browserExecutorService.getPage(executionId).waitForLoadState();
                }
            }
            case ASSERT_UI_TEXT -> {
                String expected = requiredString(params, "expected");
                boolean visible = browserExecutorService.getPage(executionId).locator("text=" + expected).first().isVisible();
                if (!visible) {
                    throw new IllegalStateException("Expected UI text not found: " + expected);
                }
            }
            case TAKE_SCREENSHOT -> browserExecutorService.screenshot(executionId, stringParam(params, "label", "step"));
            case ADD_RBA_ADAPTER -> {
                RiskManagementPage risk = new RiskManagementPage(browserExecutorService.getPage(executionId), adminUrl);
                risk.openRiskManagement();
                risk.addRbaAdapter();
            }
            case CREATE_RISK_CHAIN -> {
                RiskManagementPage risk = new RiskManagementPage(browserExecutorService.getPage(executionId), adminUrl);
                risk.openRiskManagement();
                risk.createRiskChain(stringParam(params, "name", stringParam(params, "chainName", "default")));
                risk.save();
            }
            case SET_RISK_SCORE -> {
                RiskManagementPage risk = new RiskManagementPage(browserExecutorService.getPage(executionId), adminUrl);
                risk.openRiskManagement();
                risk.setFrictionlessScore(requiredInt(params, "min"), requiredInt(params, "max"));
            }
            case RUN_TRANSACTION -> runTransaction(executionId, params);
            case RUN_EMV_TRANSACTION -> {
                TransactionPage tx = new TransactionPage(browserExecutorService.getPage(executionId), brwUrl);
                tx.openTransaction();
                if (params.containsKey("version")) {
                    tx.selectEmvVersion(params.get("version").toString());
                }
                if (params.containsKey("scheme")) {
                    tx.selectScheme(params.get("scheme").toString());
                }
                tx.runTransaction();
            }
            case VERIFY_TRANSACTION_STATUS -> {
                TransactionPage tx = new TransactionPage(browserExecutorService.getPage(executionId), brwUrl);
                String actual = tx.getTransactionStatus();
                String expected = requiredString(params, "expected");
                if (!actual.contains(expected)) {
                    throw new IllegalStateException("Transaction status mismatch. expected=" + expected + ", actual=" + actual);
                }
            }
            case VERIFY_ARES, VERIFY_RREQ -> log.debug("Placeholder verification for {} with params={}", actionType, params);
            case CONFIGURE_WHITELIST, ENABLE_WHITELIST -> {
                TransactionPage tx = new TransactionPage(browserExecutorService.getPage(executionId), brwUrl);
                tx.openTransaction();
                tx.checkWhitelistCheckbox();
                tx.runTransaction();
            }
            case CONFIGURE_ISSUER_SETTINGS -> {
                IssuerSettingsPage issuer = new IssuerSettingsPage(browserExecutorService.getPage(executionId), adminUrl);
                issuer.openIssuerSettings();
                issuer.configureIssuerSettings(
                        stringParam(params, "binRange", null),
                        stringParam(params, "defaultEmvVersion", null),
                        stringParam(params, "challengePreference", null)
                );
            }
            case DISABLE_PURCHASE_DATE_VALIDATION -> {
                IssuerSettingsPage issuer = new IssuerSettingsPage(browserExecutorService.getPage(executionId), adminUrl);
                issuer.openIssuerSettings();
                issuer.disablePurchaseDateValidation();
            }
            case CONFIGURE_BIN_RANGE -> {
                IssuerSettingsPage issuer = new IssuerSettingsPage(browserExecutorService.getPage(executionId), adminUrl);
                issuer.openIssuerSettings();
                issuer.configureBinRange(requiredString(params, "binRange"));
            }
            case SET_EMV_VERSION_DEFAULT -> {
                IssuerSettingsPage issuer = new IssuerSettingsPage(browserExecutorService.getPage(executionId), adminUrl);
                issuer.openIssuerSettings();
                issuer.setDefaultEmvVersion(requiredString(params, "version"));
            }
            case SET_CHALLENGE_PREFERENCE -> {
                IssuerSettingsPage issuer = new IssuerSettingsPage(browserExecutorService.getPage(executionId), adminUrl);
                issuer.openIssuerSettings();
                issuer.setChallengePreference(requiredString(params, "preference"));
            }
            default -> log.debug("No concrete dispatcher implementation for action {}, treated as no-op", actionType.getCode());
        }
    }

    private void runTransaction(String executionId, Map<String, Object> params) {
        TransactionPage tx = new TransactionPage(browserExecutorService.getPage(executionId), brwUrl);
        tx.openTransaction();
        if (params.containsKey("scheme")) {
            tx.selectScheme(params.get("scheme").toString());
        }
        if (params.containsKey("version")) {
            tx.selectEmvVersion(params.get("version").toString());
        }
        if (params.containsKey("amount")) {
            tx.setAmount(params.get("amount").toString());
        }
        if (params.containsKey("currency")) {
            tx.setCurrency(params.get("currency").toString());
        }
        if (params.containsKey("cardNumber")) {
            tx.setCardNumber(params.get("cardNumber").toString());
        }
        if (params.containsKey("expiry")) {
            tx.setExpiry(params.get("expiry").toString());
        }
        if (params.containsKey("cvv")) {
            tx.setCvv(params.get("cvv").toString());
        }
        if (params.containsKey("purchaseDate")) {
            tx.setPurchaseDate(params.get("purchaseDate").toString());
        }
        if (params.containsKey("challengeIndicator")) {
            tx.setChallengeIndicator(params.get("challengeIndicator").toString());
        }
        if (params.containsKey("cardholderName")) {
            tx.setCardholderName(params.get("cardholderName").toString());
        }
        if (params.containsKey("email")) {
            tx.setEmail(params.get("email").toString());
        }
        if (params.containsKey("merchantCategoryCode")) {
            tx.setMerchantCategoryCode(params.get("merchantCategoryCode").toString());
        }
        if (Boolean.parseBoolean(stringParam(params, "whitelist", "false"))) {
            tx.checkWhitelistCheckbox();
        }
        tx.runTransaction();
    }

    private String requiredString(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Missing required parameter: " + key);
        }
        return value.toString();
    }

    private int requiredInt(Map<String, Object> params, String key) {
        return Integer.parseInt(requiredString(params, key));
    }

    private String stringParam(Map<String, Object> params, String key, String defaultValue) {
        Object value = params.get(key);
        return value == null ? defaultValue : value.toString();
    }
}
