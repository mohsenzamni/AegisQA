package com.aegisqa.scenario.parser;

import com.aegisqa.domain.scenario.Scenario;
import com.aegisqa.domain.scenario.ScenarioSource;

/**
 * Strategy interface for parsing different scenario format inputs into a Scenario domain object.
 */
public interface ScenarioParser {

    /**
     * Parse raw content into a Scenario.
     *
     * @param content raw scenario content
     * @param name    descriptive name for the scenario
     * @return parsed Scenario
     */
    Scenario parse(String content, String name);

    /**
     * The source format this parser handles.
     */
    ScenarioSource getSupportedSource();

    /**
     * Check if this parser can handle the given content.
     */
    default boolean canParse(String content) {
        return content != null && !content.isBlank();
    }
}
