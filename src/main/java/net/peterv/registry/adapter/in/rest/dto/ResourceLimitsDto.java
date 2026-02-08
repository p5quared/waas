package net.peterv.registry.adapter.in.rest.dto;

public record ResourceLimitsDto(
        long timeoutSeconds,
        long memoryLimitBytes
) {}
