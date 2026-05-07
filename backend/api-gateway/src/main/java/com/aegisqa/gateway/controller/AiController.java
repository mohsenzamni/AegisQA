package com.aegisqa.gateway.controller;

import com.aegisqa.ai.service.FailureSummarizerService;
import com.aegisqa.ai.service.RecoveryAssistantService;
import com.aegisqa.ai.service.SelectorHealingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST API for AI assistance endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Assistance", description = "AI-powered failure analysis, recovery and selector healing")
public class AiController {

    private final FailureSummarizerService failureSummarizer;
    private final RecoveryAssistantService recoveryAssistant;
    private final SelectorHealingService selectorHealing;

    @PostMapping("/summarize-failure")
    @Operation(summary = "Summarize a test failure")
    public ResponseEntity<Map<String, String>> summarizeFailure(@RequestBody Map<String, String> request) {
        String summary = failureSummarizer.summarizeFailure(
                request.get("executionId"),
                request.get("failedAction"),
                request.get("errorMessage"),
                request.getOrDefault("context", "")
        );
        return ResponseEntity.ok(Map.of("summary", summary != null ? summary : "AI unavailable"));
    }

    @PostMapping("/suggest-recovery")
    @Operation(summary = "Get AI recovery suggestion for a failed step")
    public ResponseEntity<Map<String, String>> suggestRecovery(@RequestBody Map<String, String> request) {
        String suggestion = recoveryAssistant.suggestRecovery(
                request.get("executionId"),
                request.get("failedAction"),
                request.get("error"),
                request.getOrDefault("currentState", "")
        );
        return ResponseEntity.ok(Map.of("suggestion", suggestion != null ? suggestion : "{}"));
    }

    @PostMapping("/heal-selector")
    @Operation(summary = "Get AI suggestion for a broken selector")
    public ResponseEntity<Map<String, String>> healSelector(@RequestBody Map<String, String> request) {
        String healed = selectorHealing.suggestAlternative(
                request.get("brokenSelector"),
                request.getOrDefault("pageSource", ""),
                request.getOrDefault("context", "")
        );
        return ResponseEntity.ok(Map.of("suggestedSelector", healed != null ? healed : ""));
    }
}
