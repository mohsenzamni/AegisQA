package com.aegisqa.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * AI-assisted failure summarization.
 * Produces human-readable explanations of test failures for QA engineers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FailureSummarizerService {

    private final ChatClient chatClient;

    /**
     * Summarize a test execution failure.
     *
     * @param executionId  the execution ID
     * @param failedAction the action that failed
     * @param errorMessage the raw error message
     * @param stepContext  surrounding step context
     * @return human-readable failure summary
     */
    public String summarizeFailure(String executionId, String failedAction,
                                   String errorMessage, String stepContext) {
        log.info("AI summarizing failure for execution {} at action {}", executionId, failedAction);
        try {
            String prompt = """
                    An ACS automated test step failed. Provide a clear, concise explanation for a QA engineer.
                    
                    Execution ID: %s
                    Failed Action: %s
                    Error: %s
                    Context: %s
                    
                    Provide:
                    1. A plain-English explanation of what likely went wrong
                    2. Possible root causes (UI change, timing, ACS config)
                    3. Recommended next steps (max 3 bullet points)
                    
                    Keep the response under 200 words.
                    """.formatted(executionId, failedAction, errorMessage, stepContext);

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.warn("Failure summarizer AI unavailable: {}", e.getMessage());
            return "Failed action: " + failedAction + " — " + errorMessage;
        }
    }

    /**
     * Generate an assertion failure explanation.
     */
    public String explainAssertionFailure(String assertion, String expected, String actual) {
        log.debug("AI explaining assertion failure: {} expected={} actual={}", assertion, expected, actual);
        try {
            String prompt = """
                    An ACS test assertion failed. Explain what this means.
                    
                    Assertion: %s
                    Expected: %s
                    Actual: %s
                    
                    Explain in 1-2 sentences what this ACS/3DS result means for the transaction
                    and whether it indicates a configuration issue or expected test behaviour.
                    """.formatted(assertion, expected, actual);

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            return "Assertion '" + assertion + "' failed: expected '" + expected + "' but got '" + actual + "'";
        }
    }
}
