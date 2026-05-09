package com.aegisqa.mcp.tool;

import com.aegisqa.browser.service.BrowserExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MCP tool: capture_screenshot — captures a screenshot of the current page state.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CaptureScreenshotTool implements McpTool {

    private final BrowserExecutorService browserService;

    @Override
    public String getName() { return "capture_screenshot"; }

    @Override
    public String getDescription() { return "Capture a screenshot of the current browser state"; }

    @Override
    public List<String> getRequiredParams() { return List.of("executionId"); }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String executionId = params.get("executionId").toString();
        String label = params.getOrDefault("label", "screenshot").toString();
        try {
            String path = browserService.screenshot(executionId, label);
            return Map.of("success", true, "data", Map.of("path", path));
        } catch (Exception e) {
            log.error("capture_screenshot failed: {}", e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}
