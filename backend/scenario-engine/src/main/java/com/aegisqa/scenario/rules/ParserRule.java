package com.aegisqa.scenario.rules;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * A rule that maps a regex pattern on scenario text to a canonical action.
 */
@Data
@Builder
public class ParserRule {

    /** Regex pattern to match against scenario text. */
    private String match;

    /** Canonical action type to emit when matched. */
    private String action;

    /** Fixed parameter values for this action. */
    @Singular("param")
    private Map<String, String> params;

    /** Capture-group parameter bindings (captureGroup index -> parameter name). */
    @Singular
    private List<CaptureGroupBinding> captureBindings;

    @Data
    @Builder
    public static class CaptureGroupBinding {
        private String paramName;
        private int groupIndex;
    }
}
