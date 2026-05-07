package com.aegisqa.reporting.generator;

import com.aegisqa.reporting.model.ExecutionReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generates machine-readable JSON execution reports.
 */
@Slf4j
@Component
public class JsonReportGenerator {

    private final ObjectMapper mapper;

    @Value("${aegisqa.reports.dir:reports}")
    private String reportsDir;

    public JsonReportGenerator() {
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String generate(ExecutionReport report) {
        try {
            Path dir = Paths.get(reportsDir, report.getExecutionId());
            Files.createDirectories(dir);
            Path file = dir.resolve("report.json");
            mapper.writeValue(file.toFile(), report);
            log.info("JSON report generated: {}", file);
            return file.toString();
        } catch (IOException e) {
            log.error("Failed to generate JSON report", e);
            return null;
        }
    }
}
