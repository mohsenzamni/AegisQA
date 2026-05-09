package com.aegisqa.domain.scenario;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * A single step in a scenario before canonical action mapping.
 */
@Data
@Builder
public class ScenarioStep {

    private int stepNumber;
    private String rawText;
    private String actionType;
    private Map<String, Object> parameters;
    private ExpectedResult expectedResult;
}
