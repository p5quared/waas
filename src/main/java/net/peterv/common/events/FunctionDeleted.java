package net.peterv.common.events;

import net.peterv.common.types.FunctionId;

import java.time.Instant;
import java.util.UUID;

public record FunctionDeleted(
        UUID eventId,
        Instant occurredAt,
        FunctionId functionId
) implements DomainEvent {

    public static FunctionDeleted now(FunctionId functionId) {
        return new FunctionDeleted(UUID.randomUUID(), Instant.now(), functionId);
    }
}
