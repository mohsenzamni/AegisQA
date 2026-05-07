package com.aegisqa.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI configuration.
 * Connects to LiteLLM via OpenAI-compatible endpoint configured in application.yml.
 */
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        You are an AI assistant for the AegisQA platform — an enterprise ACS (Access Control Server)
                        test automation platform. You help with:
                        1. Interpreting semi-structured test scenarios into canonical actions
                        2. Suggesting selector healing when UI elements cannot be found
                        3. Providing recovery suggestions for failed test steps
                        4. Summarizing test failures for engineers
                        
                        Always respond with structured JSON when a schema is requested.
                        Never directly execute browser actions — only suggest them for human/deterministic validation.
                        Never include secrets, PANs, or authentication credentials in your responses.
                        """)
                .build();
    }
}
