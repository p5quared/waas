package net.peterv.common.events;

import net.peterv.common.types.FunctionId;
import net.peterv.common.types.ModuleReference;

import java.time.Instant;
import java.util.UUID;

public record FunctionCreated(
        UUID eventId,
        Instant occurredAt,
        FunctionId functionId,
        String name,
        ModuleReference moduleReference
) implements DomainEvent {

    public static FunctionCreated now(FunctionId functionId, String name, ModuleReference moduleReference) {
        return new FunctionCreated(UUID.randomUUID(), Instant.now(), functionId, name, moduleReference);
    }
}
