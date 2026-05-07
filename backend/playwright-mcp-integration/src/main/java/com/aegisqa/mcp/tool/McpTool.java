package com.aegisqa.mcp.tool;

import java.util.Map;

/**
 * MCP-compatible tool interface.
 * All browser tools are wrapped as MCP tools for structured, validated invocation.
 */
public interface McpTool {

    /**
     * Unique tool name (e.g. "open_page", "click_element").
     */
    String getName();

    /**
     * Human-readable description of what this tool does.
     */
    String getDescription();

    /**
     * Execute the tool with the given parameters.
     *
     * @param params tool-specific parameters
     * @return a result map (contains "success", "data", optional "error")
     */
    Map<String, Object> execute(Map<String, Object> params);

    /**
     * List of required parameter names for validation.
     */
    default java.util.List<String> getRequiredParams() {
        return java.util.List.of();
    }
}
