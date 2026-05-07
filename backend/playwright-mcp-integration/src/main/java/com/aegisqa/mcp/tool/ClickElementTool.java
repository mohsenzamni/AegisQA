package com.aegisqa.mcp.tool;

import com.aegisqa.browser.service.BrowserExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MCP tool: click_element — clicks a UI element.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClickElementTool implements McpTool {

    private final BrowserExecutorService browserService;

    @Override
    public String getName() { return "click_element"; }

    @Override
    public String getDescription() { return "Click a UI element by CSS selector"; }

    @Override
    public List<String> getRequiredParams() { return List.of("executionId", "selector"); }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String executionId = params.get("executionId").toString();
        String selector = params.get("selector").toString();
        try {
            browserService.click(executionId, selector);
            return Map.of("success", true, "data", Map.of("selector", selector));
        } catch (Exception e) {
            log.error("click_element failed for {}: {}", selector, e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}
