package com.aegisqa.gateway.controller;

import com.aegisqa.mcp.registry.McpToolRegistry;
import com.aegisqa.mcp.tool.McpTool;
import com.aegisqa.mcp.validator.McpToolValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * REST API for MCP tool management and invocation.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/mcp")
@RequiredArgsConstructor
@Tag(name = "MCP Tools", description = "MCP-compatible browser automation tools")
public class McpController {

    private final McpToolRegistry toolRegistry;
    private final McpToolValidator toolValidator;

    @GetMapping("/tools")
    @Operation(summary = "List all available MCP tools")
    public ResponseEntity<Map<String, String>> listTools() {
        return ResponseEntity.ok(toolRegistry.describeAll());
    }

    @PostMapping("/tools/{toolName}/execute")
    @Operation(summary = "Execute an MCP tool")
    public ResponseEntity<Map<String, Object>> executeTool(@PathVariable String toolName,
                                                            @RequestBody Map<String, Object> params) {
        McpToolValidator.ValidationResult validation = toolValidator.validate(toolName, params);
        if (!validation.valid()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", validation.errorMessage()));
        }

        Optional<McpTool> tool = toolRegistry.get(toolName);
        if (tool.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        log.info("Executing MCP tool: {} with params={}", toolName, params.keySet());
        Map<String, Object> result = tool.get().execute(params);
        return ResponseEntity.ok(result);
    }
}
