package com.aegisqa.scenario.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads parser rules from parser-rules.yaml at startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RuleBasedParser {

    private final ParserRuleRegistry registry;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @PostConstruct
    public void loadRules() {
        try {
            ClassPathResource resource = new ClassPathResource("parser-rules.yaml");
            if (!resource.exists()) {
                log.warn("parser-rules.yaml not found on classpath; no rules loaded");
                return;
            }
            try (InputStream is = resource.getInputStream()) {
                JsonNode root = yamlMapper.readTree(is);
                JsonNode rulesNode = root.path("rules");
                if (!rulesNode.isArray()) {
                    log.warn("parser-rules.yaml has no 'rules' array");
                    return;
                }
                int count = 0;
                for (JsonNode ruleNode : rulesNode) {
                    ParserRule rule = parseRule(ruleNode);
                    registry.register(rule);
                    count++;
                }
                log.info("Loaded {} parser rules from parser-rules.yaml", count);
            }
        } catch (IOException e) {
            log.error("Failed to load parser rules", e);
        }
    }

    private ParserRule parseRule(JsonNode node) {
        String match = node.path("match").asText();
        String action = node.path("action").asText();

        Map<String, String> params = new HashMap<>();
        List<ParserRule.CaptureGroupBinding> bindings = new ArrayList<>();

        JsonNode parametersNode = node.path("parameters");
        if (parametersNode.isArray()) {
            for (JsonNode param : parametersNode) {
                String paramName = param.path("name").asText();
                if (param.has("value")) {
                    params.put(paramName, param.path("value").asText());
                } else if (param.has("captureGroup")) {
                    int group = param.path("captureGroup").asInt();
                    bindings.add(ParserRule.CaptureGroupBinding.builder()
                            .paramName(paramName)
                            .groupIndex(group)
                            .build());
                }
            }
        }

        return ParserRule.builder()
                .match(match)
                .action(action)
                .params(params)
                .captureBindings(bindings)
                .build();
    }
}
