package com.aegisqa.mcp.tool;

import com.aegisqa.browser.service.BrowserExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MCP tool: fill_input — fills a form field with a value.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FillInputTool implements McpTool {

    private final BrowserExecutorService browserService;

    @Override
    public String getName() { return "fill_input"; }

    @Override
    public String getDescription() { return "Fill a form input field with a value"; }

    @Override
    public List<String> getRequiredParams() { return List.of("executionId", "selector", "value"); }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String executionId = params.get("executionId").toString();
        String selector = params.get("selector").toString();
        String value = params.get("value").toString();
        try {
            browserService.fill(executionId, selector, value);
            return Map.of("success", true, "data", Map.of("selector", selector));
        } catch (Exception e) {
            log.error("fill_input failed for {}: {}", selector, e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}
