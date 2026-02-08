package net.peterv.registry.adapter.in.rest.dto;

public record FunctionResponse(
        String id,
        String name,
        String contentHash,
        long timeoutSeconds,
        long memoryLimitBytes
) {}
