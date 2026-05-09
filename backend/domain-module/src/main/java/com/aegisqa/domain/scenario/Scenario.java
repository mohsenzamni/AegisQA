package com.aegisqa.domain.scenario;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * Represents a parsed, normalized test scenario ready for canonical action mapping.
 */
@Data
@Builder
public class Scenario {

    private String id;
    private String name;
    private String description;
    private ScenarioSource source;

    @Singular
    private List<ScenarioStep> steps;

    @Singular("metadata")
    private Map<String, String> metadata;

    private String rawContent;
}
