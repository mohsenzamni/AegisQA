package com.aegisqa.domain.scenario;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * Expected outcome for a scenario step.
 */
@Data
@Builder
public class ExpectedResult {

    @Singular
    private List<String> assertions;

    @Singular("field")
    private Map<String, String> fields;

    private String description;
}
