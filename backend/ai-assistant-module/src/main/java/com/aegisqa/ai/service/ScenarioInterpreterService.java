package com.aegisqa.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Uses AI to interpret semi-structured scenario text into canonical action JSON.
 * This is a fallback path when rule-based parsing fails.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioInterpreterService {

    private final ChatClient chatClient;

    /**
     * Interpret a plain-text scenario line into a canonical action JSON string.
     *
     * @param line the raw scenario text line
     * @return JSON string: {"action": "...", "parameters": {...}} or null if AI is unavailable
     */
    public String interpretLine(String line) {
        log.debug("AI interpreting scenario line: {}", line);
        try {
            String prompt = """
                    Convert the following ACS test scenario step to a canonical action JSON:
                    
                    Step: "%s"
                    
                    Available actions: add_rba_adapter, create_risk_chain, set_risk_score, run_transaction,
                    run_emv_transaction, verify_transaction_status, verify_ares, verify_rreq,
                    configure_whitelist, enable_whitelist, navigate_to, login, take_screenshot
                    
                    Respond ONLY with valid JSON in this format:
                    {"action": "<action_name>", "parameters": {"key": "value"}}
                    """.formatted(line);

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.warn("AI interpretation unavailable for line '{}': {}", line, e.getMessage());
            return null;
        }
    }

    /**
     * Interpret a complete multi-line scenario into a list of canonical action steps.
     */
    public String interpretScenario(String scenarioText) {
        log.info("AI interpreting full scenario ({} chars)", scenarioText.length());
        try {
            String prompt = """
                    Parse the following ACS test scenario into a list of canonical actions.
                    
                    Scenario:
                    ---
                    %s
                    ---
                    
                    Respond ONLY with a JSON array of steps:
                    [{"action": "...", "parameters": {...}}, ...]
                    """.formatted(scenarioText);

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.warn("AI scenario interpretation failed: {}", e.getMessage());
            return null;
        }
    }
}
