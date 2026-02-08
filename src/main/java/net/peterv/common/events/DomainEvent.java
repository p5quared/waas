package net.peterv.common.events;

import net.peterv.common.types.FunctionId;

import java.time.Instant;
import java.util.UUID;

public sealed interface DomainEvent permits FunctionCreated, FunctionUpdated, FunctionDeleted {

    UUID eventId();

    Instant occurredAt();

    FunctionId functionId();
}
