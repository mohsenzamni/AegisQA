package com.aegisqa.gateway.controller;

import com.aegisqa.domain.execution.ExecutionState;
import com.aegisqa.domain.scenario.Scenario;
import com.aegisqa.domain.scenario.ScenarioSource;
import com.aegisqa.gateway.dto.ExecutionResponse;
import com.aegisqa.gateway.dto.ScenarioRequest;
import com.aegisqa.orchestration.service.OrchestrationService;
import com.aegisqa.scenario.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * REST API for scenario execution management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/executions")
@RequiredArgsConstructor
@Tag(name = "Executions", description = "Scenario execution lifecycle management")
public class ExecutionController {

    private final ScenarioService scenarioService;
    private final OrchestrationService orchestrationService;

    @PostMapping("/run")
    @Operation(summary = "Run a scenario", description = "Parses and executes a scenario, returning the execution ID")
    public ResponseEntity<Map<String, String>> runScenario(@Valid @RequestBody ScenarioRequest request) {
        log.info("Running scenario: {}", request.getName());
        Scenario scenario = scenarioService.parseAutoDetect(request.getContent(), request.getName());
        String executionId = orchestrationService.startExecution(scenario);
        return ResponseEntity.accepted().body(Map.of("executionId", executionId));
    }

    @GetMapping("/{executionId}")
    @Operation(summary = "Get execution state", description = "Returns current state of a running or completed execution")
    public ResponseEntity<ExecutionResponse> getExecution(@PathVariable String executionId) {
        Optional<ExecutionState> state = orchestrationService.getState(executionId);
        return state.map(s -> ResponseEntity.ok(toResponse(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{executionId}/cancel")
    @Operation(summary = "Cancel execution")
    public ResponseEntity<Void> cancel(@PathVariable String executionId) {
        orchestrationService.cancel(executionId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{executionId}/steps/{stepNumber}/retry")
    @Operation(summary = "Retry a failed step")
    public ResponseEntity<Void> retryStep(@PathVariable String executionId,
                                           @PathVariable int stepNumber) {
        orchestrationService.retryStep(executionId, stepNumber);
        return ResponseEntity.accepted().build();
    }

    private ExecutionResponse toResponse(ExecutionState state) {
        return ExecutionResponse.builder()
                .executionId(state.getExecutionId())
                .scenarioId(state.getScenarioId())
                .status(state.getStatus())
                .currentStep(state.getCurrentStep())
                .totalSteps(state.getTotalSteps())
                .transactionId(state.getTransactionId())
                .startedAt(state.getStartedAt())
                .completedAt(state.getCompletedAt())
                .lastError(state.getLastError())
                .retryCount(state.getRetryCount())
                .build();
    }
}
