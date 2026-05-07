package com.aegisqa.scenario.parser;

import com.aegisqa.domain.scenario.Scenario;
import com.aegisqa.domain.scenario.ScenarioSource;
import com.aegisqa.domain.scenario.ScenarioStep;
import com.aegisqa.scenario.rules.ParserRuleRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Parses YAML scenarios in the canonical action DSL format.
 *
 * Expected structure:
 * <pre>
 * name: My Scenario
 * steps:
 *   - action: create_risk_chain
 *     name: default_chain
 *   - action: run_transaction
 *     scheme: visa
 * </pre>
 */
@Slf4j
@Component
public class YamlScenarioParser implements ScenarioParser {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final ParserRuleRegistry ruleRegistry;

    public YamlScenarioParser(ParserRuleRegistry ruleRegistry) {
        this.ruleRegistry = ruleRegistry;
    }

    @Override
    public Scenario parse(String content, String name) {
        log.debug("Parsing YAML scenario: {}", name);
        try {
            JsonNode root = yamlMapper.readTree(content);
            String scenarioName = root.path("name").asText(name);
            String description = root.path("description").asText("");
            JsonNode stepsNode = root.path("steps");

            List<ScenarioStep> steps = new ArrayList<>();
            if (stepsNode.isArray()) {
                int stepNum = 1;
                for (JsonNode stepNode : stepsNode) {
                    String actionType = stepNode.path("action").asText("unknown");
                    Map<String, Object> params = new HashMap<>();
                    stepNode.fields().forEachRemaining(entry -> {
                        if (!"action".equals(entry.getKey())) {
                            params.put(entry.getKey(), entry.getValue().asText());
                        }
                    });
                    steps.add(ScenarioStep.builder()
                            .stepNumber(stepNum++)
                            .rawText(actionType)
                            .actionType(actionType)
                            .parameters(params)
                            .build());
                }
            }

            return Scenario.builder()
                    .id(UUID.randomUUID().toString())
                    .name(scenarioName)
                    .description(description)
                    .source(ScenarioSource.YAML)
                    .steps(steps)
                    .rawContent(content)
                    .build();
        } catch (IOException e) {
            log.error("Failed to parse YAML scenario", e);
            throw new IllegalArgumentException("Invalid YAML scenario content: " + e.getMessage(), e);
        }
    }

    @Override
    public ScenarioSource getSupportedSource() {
        return ScenarioSource.YAML;
    }

    @Override
    public boolean canParse(String content) {
        return content != null && (content.contains("steps:") || content.contains("action:"));
    }
}
