package com.aegisqa.domain.scenario;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScenarioSource {
    YAML("yaml", "YAML scenario file"),
    MARKDOWN("md", "Markdown scenario file"),
    EXCEL("xlsx", "Excel test case spreadsheet"),
    PLAIN_TEXT("txt", "Plain text scenario"),
    JSON("json", "JSON scenario file");

    private final String extension;
    private final String description;
}
