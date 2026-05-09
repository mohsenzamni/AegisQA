package com.aegisqa.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * AI-assisted recovery suggestions for failed test steps.
 * Suggests deterministic recovery actions that the executor can validate and apply.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecoveryAssistantService {

    private final ChatClient chatClient;

    /**
     * Suggest a recovery action for a failed step.
     *
     * @param executionId  execution ID
     * @param failedAction the canonical action that failed
     * @param error        the error message
     * @param currentState current execution state description
     * @return JSON suggestion: {"recoveryAction": "...", "params": {...}, "reason": "..."}
     */
    public String suggestRecovery(String executionId, String failedAction,
                                  String error, String currentState) {
        log.info("AI suggesting recovery for {} in execution {}", failedAction, executionId);
        try {
            String prompt = """
                    An ACS test step failed and needs a recovery suggestion.
                    
                    Failed Action: %s
                    Error: %s
                    Current State: %s
                    
                    Available recovery actions:
                    - retry_step: Retry the failed step
                    - dismiss_modal: Dismiss a blocking modal dialog
                    - restore_session: Re-login and restore browser session
                    - skip_step: Skip this step and continue
                    - reload_page: Reload the current page
                    
                    Respond ONLY with JSON:
                    {"recoveryAction": "...", "params": {}, "reason": "..."}
                    """.formatted(failedAction, error, currentState);

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.warn("Recovery assistant AI unavailable: {}", e.getMessage());
            return "{\"recoveryAction\": \"retry_step\", \"reason\": \"AI unavailable, defaulting to retry\"}";
        }
    }
}
