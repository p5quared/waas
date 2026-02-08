package net.peterv.registry.domain.model;

import java.time.Duration;

public record ResourceLimits(Duration timeout, long memoryLimitBytes) {

    public static final ResourceLimits DEFAULTS = new ResourceLimits(Duration.ofSeconds(30), 64 * 1024 * 1024);

    public ResourceLimits {
        if (timeout.isNegative()) {
            throw new IllegalArgumentException("Timeout must not be negative");
        }
        if (memoryLimitBytes <= 0) {
            throw new IllegalArgumentException("Memory limit must be greater than zero");
        }
    }
}
