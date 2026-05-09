package com.aegisqa.scenario.rules;

import com.aegisqa.domain.scenario.ScenarioStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Registry of parser rules. Supports registration of rules at startup and runtime matching.
 */
@Slf4j
@Component
public class ParserRuleRegistry {

    private final List<CompiledRule> rules = new ArrayList<>();

    /**
     * Register a parser rule.
     */
    public void register(ParserRule rule) {
        rules.add(new CompiledRule(rule, Pattern.compile(rule.getMatch(), Pattern.CASE_INSENSITIVE)));
        log.debug("Registered parser rule: {} -> {}", rule.getMatch(), rule.getAction());
    }

    /**
     * Try to match a line of text against all registered rules.
     * Returns a ScenarioStep if matched, null otherwise.
     */
    public ScenarioStep matchLine(String line, int stepNumber) {
        for (CompiledRule cr : rules) {
            Matcher m = cr.pattern().matcher(line);
            if (m.find()) {
                Map<String, Object> params = new HashMap<>();
                // Add fixed params
                if (cr.rule().getParams() != null) {
                    params.putAll(cr.rule().getParams());
                }
                // Add capture-group params
                if (cr.rule().getCaptureBindings() != null) {
                    for (ParserRule.CaptureGroupBinding binding : cr.rule().getCaptureBindings()) {
                        try {
                            String captured = m.group(binding.getGroupIndex());
                            if (captured != null) {
                                params.put(binding.getParamName(), captured);
                            }
                        } catch (IndexOutOfBoundsException e) {
                            log.warn("Capture group {} not found in match for rule {}", binding.getGroupIndex(), cr.rule().getAction());
                        }
                    }
                }
                log.debug("Rule matched: '{}' -> action={}", line, cr.rule().getAction());
                return ScenarioStep.builder()
                        .stepNumber(stepNumber)
                        .rawText(line)
                        .actionType(cr.rule().getAction())
                        .parameters(Collections.unmodifiableMap(params))
                        .build();
            }
        }
        return null;
    }

    public List<ParserRule> getAllRules() {
        return rules.stream().map(CompiledRule::rule).toList();
    }

    private record CompiledRule(ParserRule rule, Pattern pattern) {}
}
