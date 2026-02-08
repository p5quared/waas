package net.peterv.registry.adapter.in.rest.dto;

public record UpdateFunctionRequest(
        String description,
        byte[] wasmBytes,
        ResourceLimitsDto resourceLimits
) {}
