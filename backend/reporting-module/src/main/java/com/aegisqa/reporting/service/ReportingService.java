package com.aegisqa.reporting.service;

import com.aegisqa.domain.assertion.AssertionResult;
import com.aegisqa.domain.execution.ExecutionState;
import com.aegisqa.domain.execution.StepResult;
import com.aegisqa.reporting.generator.HtmlReportGenerator;
import com.aegisqa.reporting.generator.JsonReportGenerator;
import com.aegisqa.reporting.model.ExecutionReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Coordinates report generation from execution state.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportingService {

    private final HtmlReportGenerator htmlGenerator;
    private final JsonReportGenerator jsonGenerator;

    /**
     * Generate a complete execution report from execution state.
     */
    public ExecutionReport generateReport(ExecutionState state, String scenarioName) {
        log.info("Generating report for execution {}", state.getExecutionId());

        List<StepResult> steps = state.getStepResults();
        long passed = steps.stream().filter(StepResult::isPassed).count();
        long failed = steps.stream().filter(StepResult::isFailed).count();

        List<AssertionResult> allAssertions = steps.stream()
                .flatMap(s -> s.getAssertionResults().stream())
                .toList();

        long durationMs = 0;
        if (state.getStartedAt() != null) {
            Instant end = state.getCompletedAt() != null ? state.getCompletedAt() : Instant.now();
            durationMs = end.toEpochMilli() - state.getStartedAt().toEpochMilli();
        }

        ExecutionReport report = ExecutionReport.builder()
                .reportId(UUID.randomUUID().toString())
                .executionId(state.getExecutionId())
                .scenarioId(state.getScenarioId())
                .scenarioName(scenarioName)
                .finalStatus(state.getStatus())
                .startedAt(state.getStartedAt())
                .completedAt(state.getCompletedAt())
                .durationMs(durationMs)
                .totalSteps(state.getTotalSteps())
                .passedSteps((int) passed)
                .failedSteps((int) failed)
                .stepResults(steps)
                .allAssertions(allAssertions)
                .build();

        String htmlPath = htmlGenerator.generate(report);
        String jsonPath = jsonGenerator.generate(report);

        return ExecutionReport.builder()
                .reportId(report.getReportId())
                .executionId(report.getExecutionId())
                .scenarioId(report.getScenarioId())
                .scenarioName(report.getScenarioName())
                .finalStatus(report.getFinalStatus())
                .startedAt(report.getStartedAt())
                .completedAt(report.getCompletedAt())
                .durationMs(report.getDurationMs())
                .totalSteps(report.getTotalSteps())
                .passedSteps(report.getPassedSteps())
                .failedSteps(report.getFailedSteps())
                .stepResults(steps)
                .allAssertions(allAssertions)
                .htmlReportPath(htmlPath)
                .jsonReportPath(jsonPath)
                .build();
    }
}
