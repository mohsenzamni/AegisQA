package com.aegisqa.gateway.controller;

import com.aegisqa.domain.scenario.Scenario;
import com.aegisqa.domain.scenario.ScenarioSource;
import com.aegisqa.gateway.dto.ScenarioRequest;
import com.aegisqa.scenario.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for scenario parsing and validation.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/scenarios")
@RequiredArgsConstructor
@Tag(name = "Scenarios", description = "Scenario parsing and management")
public class ScenarioController {

    private final ScenarioService scenarioService;

    @PostMapping("/parse")
    @Operation(summary = "Parse a scenario from content", description = "Parses YAML, Markdown, Excel, or plain text into canonical actions")
    public ResponseEntity<Scenario> parse(@Valid @RequestBody ScenarioRequest request) {
        log.info("Parsing scenario: {}", request.getName());
        Scenario scenario;
        if (request.getSource() == null || request.getSource().isBlank()) {
            scenario = scenarioService.parseAutoDetect(request.getContent(), request.getName());
        } else {
            ScenarioSource source = ScenarioSource.valueOf(request.getSource().toUpperCase());
            scenario = scenarioService.parse(request.getContent(), request.getName(), source);
        }
        return ResponseEntity.ok(scenario);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate a scenario", description = "Checks that all steps map to known canonical actions")
    public ResponseEntity<ValidationResult> validate(@Valid @RequestBody ScenarioRequest request) {
        Scenario scenario = scenarioService.parseAutoDetect(request.getContent(), request.getName());
        long unknownSteps = scenario.getSteps().stream()
                .filter(s -> "unknown".equals(s.getActionType()))
                .count();
        boolean valid = unknownSteps == 0;
        return ResponseEntity.ok(new ValidationResult(valid, scenario.getSteps().size(),
                (int) unknownSteps, valid ? "All steps recognized" : unknownSteps + " steps could not be parsed"));
    }

    public record ValidationResult(boolean valid, int totalSteps, int unknownSteps, String message) {}
}
