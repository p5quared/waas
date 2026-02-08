package net.peterv.registry.application.usecase;

import net.peterv.common.types.FunctionId;
import net.peterv.registry.domain.model.ResourceLimits;

public final class FunctionUseCases {

    private FunctionUseCases() {}

    public record CreateFunction(
            String name,
            String description,
            byte[] wasmBytes,
            ResourceLimits resourceLimits
    ) {}

    public record UpdateFunction(
            FunctionId id,
            String description,
            byte[] wasmBytes,
            ResourceLimits resourceLimits
    ) {}

    public record DeleteFunction(FunctionId id) {}

    public record GetFunction(FunctionId id) {}

    public record ListFunctions() {}
}
