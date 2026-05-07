package com.aegisqa.recovery.detector;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Detects ACS session expiration indicators (login page, session timeout messages).
 */
@Slf4j
@Component
public class SessionExpirationDetector implements RecoveryDetector {

    private static final String[] SESSION_INDICATORS = {
            "text=Session expired",
            "text=Your session has expired",
            "text=Please log in",
            "[data-testid='login-form']",
            "input[name='username']"
    };

    @Override
    public boolean detect(Page page) {
        for (String indicator : SESSION_INDICATORS) {
            try {
                if (page.locator(indicator).isVisible()) {
                    log.warn("Session expiration detected with indicator: {}", indicator);
                    return true;
                }
            } catch (Exception e) {
                // Continue
            }
        }
        return false;
    }

    @Override
    public RecoveryAction suggestedAction() {
        return RecoveryAction.RESTORE_SESSION;
    }

    @Override
    public String description() {
        return "Detects ACS session expiration";
    }
}
