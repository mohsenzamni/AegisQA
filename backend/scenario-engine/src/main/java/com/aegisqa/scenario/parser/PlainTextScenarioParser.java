package com.aegisqa.scenario.parser;

import com.aegisqa.domain.scenario.Scenario;
import com.aegisqa.domain.scenario.ScenarioSource;
import com.aegisqa.domain.scenario.ScenarioStep;
import com.aegisqa.scenario.rules.ParserRuleRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Parses plain-text scenarios line by line, applying rule-based mappings to canonical actions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlainTextScenarioParser implements ScenarioParser {

    private final ParserRuleRegistry ruleRegistry;

    @Override
    public Scenario parse(String content, String name) {
        log.debug("Parsing plain-text scenario: {}", name);
        List<String> lines = content.lines()
                .map(String::trim)
                .filter(l -> !l.isBlank())
                .filter(l -> !l.startsWith("#"))
                .toList();

        List<ScenarioStep> steps = new ArrayList<>();
        int stepNum = 1;
        for (String line : lines) {
            ScenarioStep step = ruleRegistry.matchLine(line, stepNum);
            if (step != null) {
                steps.add(step);
                stepNum++;
            } else {
                log.trace("No rule matched line: {}", line);
                // Include as a raw step without resolved action
                steps.add(ScenarioStep.builder()
                        .stepNumber(stepNum++)
                        .rawText(line)
                        .actionType("unknown")
                        .build());
            }
        }

        return Scenario.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .source(ScenarioSource.PLAIN_TEXT)
                .steps(steps)
                .rawContent(content)
                .build();
    }

    @Override
    public ScenarioSource getSupportedSource() {
        return ScenarioSource.PLAIN_TEXT;
    }
}
