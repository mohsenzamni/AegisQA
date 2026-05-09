package com.aegisqa.mcp.tool;

import com.aegisqa.browser.service.BrowserExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MCP tool: open_page — navigates the browser to a URL.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenPageTool implements McpTool {

    private final BrowserExecutorService browserService;

    @Override
    public String getName() { return "open_page"; }

    @Override
    public String getDescription() { return "Navigate the browser to a URL"; }

    @Override
    public List<String> getRequiredParams() { return List.of("executionId", "url"); }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String executionId = params.get("executionId").toString();
        String url = params.get("url").toString();
        try {
            browserService.navigate(executionId, url);
            return Map.of("success", true, "data", Map.of("url", url));
        } catch (Exception e) {
            log.error("open_page failed for {}: {}", url, e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}
