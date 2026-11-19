package com.lumafeed.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Validates the server-to-server token used by LumenPilot.
 *
 * @author qzh
 */
@Component
public class AgentAccessGuard {

    @Value("${lumafeed.agent-token:luma-demo-token}")
    private String agentToken;

    public boolean isAuthorized(String token) {
        return agentToken != null && !agentToken.trim().isEmpty() && agentToken.equals(token);
    }
}
