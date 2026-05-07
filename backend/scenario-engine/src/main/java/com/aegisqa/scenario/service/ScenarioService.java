package com.aegisqa.scenario.service;

import com.aegisqa.domain.scenario.Scenario;
import com.aegisqa.domain.scenario.ScenarioSource;
import com.aegisqa.scenario.parser.ExcelScenarioParser;
import com.aegisqa.scenario.parser.MarkdownScenarioParser;
import com.aegisqa.scenario.parser.PlainTextScenarioParser;
import com.aegisqa.scenario.parser.ScenarioParser;
import com.aegisqa.scenario.parser.YamlScenarioParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Central service for parsing scenarios from various formats.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final PlainTextScenarioParser plainTextParser;
    private final MarkdownScenarioParser markdownParser;
    private final YamlScenarioParser yamlParser;
    private final ExcelScenarioParser excelParser;

    /**
     * Parse scenario content given an explicit source format.
     */
    public Scenario parse(String content, String name, ScenarioSource source) {
        log.info("Parsing scenario '{}' from source={}", name, source);
        ScenarioParser parser = selectParser(source);
        return parser.parse(content, name);
    }

    /**
     * Auto-detect format from content and parse.
     */
    public Scenario parseAutoDetect(String content, String name) {
        ScenarioSource source = autoDetect(content);
        log.info("Auto-detected scenario source: {} for '{}'", source, name);
        return parse(content, name, source);
    }

    private ScenarioSource autoDetect(String content) {
        if (content == null) return ScenarioSource.PLAIN_TEXT;
        String trimmed = content.trim();
        if (trimmed.startsWith("---") || trimmed.contains("steps:") || trimmed.contains("action:")) {
            return ScenarioSource.YAML;
        }
        if (trimmed.startsWith("#") || trimmed.contains("\n- ") || trimmed.contains("\n* ")) {
            return ScenarioSource.MARKDOWN;
        }
        // base64 check for Excel
        if (trimmed.matches("^[A-Za-z0-9+/=\\s]+$") && trimmed.length() > 200) {
            return ScenarioSource.EXCEL;
        }
        return ScenarioSource.PLAIN_TEXT;
    }

    private ScenarioParser selectParser(ScenarioSource source) {
        return switch (source) {
            case YAML -> yamlParser;
            case MARKDOWN -> markdownParser;
            case EXCEL -> excelParser;
            default -> plainTextParser;
        };
    }
}
