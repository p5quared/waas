package net.peterv.registry.domain.model;

import net.peterv.common.events.DomainEvent;
import net.peterv.common.events.FunctionCreated;
import net.peterv.common.events.FunctionUpdated;
import net.peterv.common.types.FunctionId;
import net.peterv.common.types.FunctionMetadata;
import net.peterv.common.types.ModuleReference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Function {

    private final FunctionId id;
    private String name;
    private String description;
    private ModuleReference moduleReference;
    private ResourceLimits resourceLimits;
    private final Instant createdAt;
    private Instant updatedAt;

    private final List<DomainEvent> pendingEvents = new ArrayList<>();

    private Function(
            FunctionId id,
            String name,
            String description,
            ModuleReference moduleReference,
            ResourceLimits resourceLimits,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = validateName(name);
        this.description = description != null ? description : "";
        this.moduleReference = Objects.requireNonNull(moduleReference);
        this.resourceLimits = Objects.requireNonNull(resourceLimits);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Function create(
            String name,
            String description,
            ModuleReference moduleReference,
            ResourceLimits resourceLimits
    ) {
        var now = Instant.now();
        var id = FunctionId.generate();
        var function = new Function(
                id, name, description, moduleReference,
                resourceLimits, now, now
        );
        function.pendingEvents.add(
                FunctionCreated.now(id, name, moduleReference)
        );
        return function;
    }

    public static Function reconstitute(
            FunctionId id,
            String name,
            String description,
            ModuleReference moduleReference,
            ResourceLimits resourceLimits,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Function(
                id, name, description, moduleReference,
                resourceLimits, createdAt, updatedAt
        );
    }

    public void updateModule(ModuleReference newModule) {
        Objects.requireNonNull(newModule);
        if (this.moduleReference.equals(newModule)) {
            return;
        }
        var previous = this.moduleReference;
        this.moduleReference = newModule;
        this.updatedAt = Instant.now();
        this.pendingEvents.add(FunctionUpdated.now(this.id, previous, newModule));
    }

    public void updateConfiguration(
            String description,
            ResourceLimits newLimits
    ) {
        if (description != null) {
            this.description = description;
        }
        if (newLimits != null) {
            this.resourceLimits = newLimits;
        }
        this.updatedAt = Instant.now();
    }

    public void rename(String newName) {
        this.name = validateName(newName);
        this.updatedAt = Instant.now();
    }

    public FunctionMetadata toMetadata() {
        return new FunctionMetadata(
                id,
                name,
                moduleReference,
                resourceLimits.timeout(),
                resourceLimits.memoryLimitBytes()
        );
    }

    public List<DomainEvent> drainEvents() {
        var events = List.copyOf(pendingEvents);
        pendingEvents.clear();
        return events;
    }

    public FunctionId id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public ModuleReference moduleReference() { return moduleReference; }
    public ResourceLimits resourceLimits() { return resourceLimits; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "Function name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Function name must not be blank");
        }
        if (name.length() > 128) {
            throw new IllegalArgumentException("Function name must not exceed 128 characters");
        }
        if (!name.matches("^[a-zA-Z][a-zA-Z0-9_-]*$")) {
            throw new IllegalArgumentException(
                    "Function name must start with a letter and contain only alphanumeric characters, hyphens, and underscores"
            );
        }
        return name;
    }
}
