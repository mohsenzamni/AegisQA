package com.aegisqa.domain.action;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

/**
 * Default implementation of CanonicalAction backed by a map of parameters.
 */
@Data
@Builder
public class DefaultCanonicalAction implements CanonicalAction {

    private final String actionType;

    @Builder.Default
    private final Map<String, Object> parameters = Collections.emptyMap();

    @Override
    public String getActionType() {
        return actionType;
    }

    @Override
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
}
