package net.peterv.execution.domain.model;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

public record ExecutionContext(
        Duration timeout,
        long memoryLimitBytes,
        Map<String, String> environmentVariables
) {

    public ExecutionContext {
        Objects.requireNonNull(timeout, "Timeout must not be null");
        if (memoryLimitBytes <= 0) {
            throw new IllegalArgumentException("memoryLimitBytes must be positive");
        }
        environmentVariables = environmentVariables != null
                ? Map.copyOf(environmentVariables)
                : Map.of();
    }
}
