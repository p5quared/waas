package net.peterv.registry.adapter.in.rest.dto;

public record CreateFunctionRequest(
        String name,
        String description,
        byte[] wasmBytes,
        ResourceLimitsDto resourceLimits
) {}
