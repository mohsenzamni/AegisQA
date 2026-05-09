package com.aegisqa.mcp.tool;

import com.aegisqa.browser.service.BrowserExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MCP tool: wait_for_text — waits until text appears on page.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WaitForTextTool implements McpTool {

    private final BrowserExecutorService browserService;

    @Override
    public String getName() { return "wait_for_text"; }

    @Override
    public String getDescription() { return "Wait for a text string to appear in the page"; }

    @Override
    public List<String> getRequiredParams() { return List.of("executionId", "text"); }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String executionId = params.get("executionId").toString();
        String text = params.get("text").toString();
        try {
            browserService.getPage(executionId).waitForSelector("text=" + text);
            return Map.of("success", true, "data", Map.of("text", text));
        } catch (Exception e) {
            log.error("wait_for_text failed for '{}': {}", text, e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}
