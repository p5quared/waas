package net.peterv.common.types;

import java.time.Duration;

public record FunctionMetadata(
        FunctionId id,
        String name,
        ModuleReference moduleReference,
        Duration timeout,
        long memoryLimitBytes
) {}
