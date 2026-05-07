package com.aegisqa.domain.action;

import java.util.Map;

/**
 * Canonical action interface. Every executable step in a scenario resolves to a CanonicalAction.
 * This is the core abstraction that decouples scenario interpretation from execution.
 */
public interface CanonicalAction {
    /**
     * The unique action type identifier (e.g. "create_risk_chain").
     */
    String getActionType();

    /**
     * Parameters for this action.
     */
    Map<String, Object> getParameters();

    /**
     * Human-readable description of what this action does.
     */
    default String describe() {
        return getActionType() + " with params=" + getParameters();
    }
}
