package com.aegisqa.mcp.registry;

import com.aegisqa.mcp.tool.McpTool;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registry of all available MCP tools. Tools are auto-discovered via Spring injection.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class McpToolRegistry {

    private final List<McpTool> tools;
    private Map<String, McpTool> toolMap;

    @PostConstruct
    public void init() {
        toolMap = tools.stream().collect(Collectors.toMap(McpTool::getName, Function.identity()));
        log.info("MCP tool registry initialized with {} tools: {}", toolMap.size(), toolMap.keySet());
    }

    public Optional<McpTool> get(String name) {
        return Optional.ofNullable(toolMap.get(name));
    }

    public List<String> listTools() {
        return List.copyOf(toolMap.keySet());
    }

    public Map<String, String> describeAll() {
        return toolMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDescription()));
    }
}
