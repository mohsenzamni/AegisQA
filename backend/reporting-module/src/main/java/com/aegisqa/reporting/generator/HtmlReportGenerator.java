package com.aegisqa.reporting.generator;

import com.aegisqa.domain.assertion.AssertionResult;
import com.aegisqa.domain.execution.StepResult;
import com.aegisqa.reporting.model.ExecutionReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Generates self-contained HTML execution reports.
 */
@Slf4j
@Component
public class HtmlReportGenerator {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @Value("${aegisqa.reports.dir:reports}")
    private String reportsDir;

    public String generate(ExecutionReport report) {
        try {
            String html = buildHtml(report);
            Path dir = Paths.get(reportsDir, report.getExecutionId());
            Files.createDirectories(dir);
            Path file = dir.resolve("report.html");
            Files.writeString(file, html);
            log.info("HTML report generated: {}", file);
            return file.toString();
        } catch (IOException e) {
            log.error("Failed to generate HTML report", e);
            return null;
        }
    }

    private String buildHtml(ExecutionReport r) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>AegisQA Report – %s</title>
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; margin: 2rem; background: #f5f5f5; }
                        .card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,.1); }
                        h1 { color: #1a1a2e; }
                        .pass { color: #16a34a; font-weight: bold; }
                        .fail { color: #dc2626; font-weight: bold; }
                        .badge { display:inline-block; padding:.25em .6em; border-radius:4px; font-size:.85em; }
                        .badge-pass { background:#dcfce7; color:#15803d; }
                        .badge-fail { background:#fee2e2; color:#dc2626; }
                        table { width:100%%; border-collapse:collapse; }
                        th { background:#1a1a2e; color:white; padding:.5rem; text-align:left; }
                        td { padding:.5rem; border-bottom:1px solid #e5e7eb; }
                        .progress { background:#e5e7eb; border-radius:4px; height:1rem; }
                        .progress-bar { background:#16a34a; height:1rem; border-radius:4px; }
                    </style>
                </head>
                <body>
                """.formatted(r.getScenarioName()));

        // Header
        String statusClass = r.getFinalStatus().isTerminal() &&
                "COMPLETED".equals(r.getFinalStatus().name()) ? "pass" : "fail";
        sb.append("""
                <div class="card">
                    <h1>🛡️ AegisQA Execution Report</h1>
                    <p><strong>Execution ID:</strong> %s</p>
                    <p><strong>Scenario:</strong> %s</p>
                    <p><strong>Status:</strong> <span class="%s">%s</span></p>
                    <p><strong>Duration:</strong> %dms</p>
                    <p><strong>Started:</strong> %s</p>
                    <div style="margin-top:1rem">
                        <div class="progress"><div class="progress-bar" style="width:%.0f%%"></div></div>
                        <small>%d/%d steps passed (%.1f%%)</small>
                    </div>
                </div>
                """.formatted(
                r.getExecutionId(), r.getScenarioName(), statusClass,
                r.getFinalStatus(), r.getDurationMs(),
                r.getStartedAt() != null ? FMT.format(r.getStartedAt()) : "N/A",
                r.successRate(), r.getPassedSteps(), r.getTotalSteps(), r.successRate()
        ));

        // AI Summary
        if (r.getAiSummary() != null && !r.getAiSummary().isBlank()) {
            sb.append("""
                    <div class="card">
                        <h2>🤖 AI Summary</h2>
                        <p>%s</p>
                    </div>
                    """.formatted(r.getAiSummary()));
        }

        // Step results table
        sb.append("""
                <div class="card">
                    <h2>📋 Step Results</h2>
                    <table>
                        <tr><th>#</th><th>Action</th><th>Status</th><th>Duration</th><th>Error</th></tr>
                """);
        for (StepResult step : r.getStepResults()) {
            String badge = step.isPassed() ? "badge-pass" : "badge-fail";
            sb.append("<tr><td>%d</td><td>%s</td><td><span class='badge %s'>%s</span></td><td>%dms</td><td>%s</td></tr>"
                    .formatted(step.getStepNumber(), step.getActionType(), badge,
                            step.getStatus(), step.getDurationMs(),
                            step.getErrorMessage() != null ? step.getErrorMessage() : ""));
        }
        sb.append("</table></div>");

        // Assertions table
        if (!r.getAllAssertions().isEmpty()) {
            sb.append("""
                    <div class="card">
                        <h2>✅ Assertions</h2>
                        <table>
                            <tr><th>Assertion</th><th>Expected</th><th>Actual</th><th>Result</th></tr>
                    """);
            for (AssertionResult ar : r.getAllAssertions()) {
                String badge = ar.isPassed() ? "badge-pass" : "badge-fail";
                sb.append("<tr><td>%s</td><td>%s</td><td>%s</td><td><span class='badge %s'>%s</span></td></tr>"
                        .formatted(ar.getAssertion(), ar.getExpected(), ar.getActual(), badge, ar.getStatus()));
            }
            sb.append("</table></div>");
        }

        sb.append("</body></html>");
        return sb.toString();
    }
}
