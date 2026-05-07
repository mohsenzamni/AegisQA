package com.aegisqa.mcp.validator;

import com.aegisqa.mcp.registry.McpToolRegistry;
import com.aegisqa.mcp.tool.McpTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Validates MCP tool invocations before execution.
 * Ensures only known tools with allowed parameters are invoked.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class McpToolValidator {

    private final McpToolRegistry registry;

    /**
     * Validate that the tool exists and all required parameters are present.
     *
     * @param toolName   the requested tool name
     * @param params     provided parameters
     * @return validation result
     */
    public ValidationResult validate(String toolName, Map<String, Object> params) {
        Optional<McpTool> tool = registry.get(toolName);
        if (tool.isEmpty()) {
            return ValidationResult.fail("Unknown tool: " + toolName + ". Available: " + registry.listTools());
        }

        List<String> missing = new ArrayList<>();
        for (String required : tool.get().getRequiredParams()) {
            if (!params.containsKey(required) || params.get(required) == null) {
                missing.add(required);
            }
        }

        if (!missing.isEmpty()) {
            return ValidationResult.fail("Missing required parameters for '" + toolName + "': " + missing);
        }

        return ValidationResult.ok();
    }

    public record ValidationResult(boolean valid, String errorMessage) {
        public static ValidationResult ok() { return new ValidationResult(true, null); }
        public static ValidationResult fail(String msg) { return new ValidationResult(false, msg); }
    }
}
