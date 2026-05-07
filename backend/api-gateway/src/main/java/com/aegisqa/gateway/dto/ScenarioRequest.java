package com.aegisqa.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for scenario parse and execution endpoints.
 */
@Data
public class ScenarioRequest {

    @NotBlank(message = "Scenario content is required")
    private String content;

    @NotBlank(message = "Scenario name is required")
    private String name;

    /** Source format: YAML, MARKDOWN, EXCEL, PLAIN_TEXT, JSON. Defaults to auto-detect. */
    private String source;
}
