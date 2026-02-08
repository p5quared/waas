package net.peterv.common.events;

import net.peterv.common.types.FunctionId;
import net.peterv.common.types.ModuleReference;

import java.time.Instant;
import java.util.UUID;

public record FunctionUpdated(
        UUID eventId,
        Instant occurredAt,
        FunctionId functionId,
        ModuleReference previousModule,
        ModuleReference newModule
) implements DomainEvent {

    public static FunctionUpdated now(FunctionId functionId, ModuleReference previousModule, ModuleReference newModule) {
        return new FunctionUpdated(UUID.randomUUID(), Instant.now(), functionId, previousModule, newModule);
    }
}
