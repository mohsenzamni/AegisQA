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
 * Parses Markdown scenarios. Treats list items and non-heading lines as steps.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarkdownScenarioParser implements ScenarioParser {

    private final ParserRuleRegistry ruleRegistry;

    @Override
    public Scenario parse(String content, String name) {
        log.debug("Parsing Markdown scenario: {}", name);

        List<String> stepLines = content.lines()
                .map(String::trim)
                .filter(l -> !l.isBlank())
                .filter(l -> !l.startsWith("#"))  // skip headings
                .map(l -> l.replaceAll("^[-*]\\s+", "")) // strip list markers
                .toList();

        List<ScenarioStep> steps = new ArrayList<>();
        int stepNum = 1;
        for (String line : stepLines) {
            ScenarioStep step = ruleRegistry.matchLine(line, stepNum);
            if (step == null) {
                step = ScenarioStep.builder()
                        .stepNumber(stepNum)
                        .rawText(line)
                        .actionType("unknown")
                        .build();
            }
            steps.add(step);
            stepNum++;
        }

        return Scenario.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .source(ScenarioSource.MARKDOWN)
                .steps(steps)
                .rawContent(content)
                .build();
    }

    @Override
    public ScenarioSource getSupportedSource() {
        return ScenarioSource.MARKDOWN;
    }
}
