package net.peterv.registry.domain.model;

import net.peterv.common.events.DomainEvent;
import net.peterv.common.events.FunctionCreated;
import net.peterv.common.events.FunctionUpdated;
import net.peterv.common.types.FunctionMetadata;
import net.peterv.common.types.ModuleReference;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTest {

    private static final ModuleReference MODULE = new ModuleReference("sha256:abc123");
    private static final ResourceLimits LIMITS = ResourceLimits.DEFAULTS;

    @Test
    void createWithValidInputsProducesCorrectStateAndEvent() {
        Function fn = Function.create("my-func", "A test function", MODULE, LIMITS);

        assertNotNull(fn.id());
        assertEquals("my-func", fn.name());
        assertEquals("A test function", fn.description());
        assertEquals(MODULE, fn.moduleReference());
        assertEquals(LIMITS, fn.resourceLimits());
        assertNotNull(fn.createdAt());
        assertNotNull(fn.updatedAt());

        List<DomainEvent> events = fn.drainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(FunctionCreated.class, events.getFirst());

        FunctionCreated created = (FunctionCreated) events.getFirst();
        assertEquals(fn.id(), created.functionId());
        assertEquals("my-func", created.name());
        assertEquals(MODULE, created.moduleReference());
    }

    @Test
    void createWithNullNameThrowsIAE() {
        assertThrows(NullPointerException.class,
                () -> Function.create(null, null, MODULE, LIMITS));
    }

    @Test
    void createWithBlankNameThrowsIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> Function.create("  ", null, MODULE, LIMITS));
    }

    @Test
    void updateModuleIdempotency() {
        Function fn = Function.create("my-func", null, MODULE, LIMITS);
        fn.drainEvents();

        fn.updateModule(MODULE);

        List<DomainEvent> events = fn.drainEvents();
        assertTrue(events.isEmpty());
        assertEquals(MODULE, fn.moduleReference());
    }

    @Test
    void updateModuleWithNewRefEmitsEvent() {
        Function fn = Function.create("my-func", null, MODULE, LIMITS);
        fn.drainEvents();

        ModuleReference newModule = new ModuleReference("sha256:def456");
        fn.updateModule(newModule);

        List<DomainEvent> events = fn.drainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(FunctionUpdated.class, events.getFirst());

        FunctionUpdated updated = (FunctionUpdated) events.getFirst();
        assertEquals(fn.id(), updated.functionId());
        assertEquals(MODULE, updated.previousModule());
        assertEquals(newModule, updated.newModule());
        assertEquals(newModule, fn.moduleReference());
    }

    @Test
    void drainEventsReturnsThenClears() {
        Function fn = Function.create("my-func", null, MODULE, LIMITS);

        List<DomainEvent> first = fn.drainEvents();
        assertEquals(1, first.size());

        List<DomainEvent> second = fn.drainEvents();
        assertTrue(second.isEmpty());
    }

    @Test
    void toMetadataProjectionIsCorrect() {
        Function fn = Function.create("my-func", "desc", MODULE, LIMITS);

        FunctionMetadata meta = fn.toMetadata();

        assertEquals(fn.id(), meta.id());
        assertEquals("my-func", meta.name());
        assertEquals(MODULE, meta.moduleReference());
        assertEquals(LIMITS.timeout(), meta.timeout());
        assertEquals(LIMITS.memoryLimitBytes(), meta.memoryLimitBytes());
    }

    @Test
    void createWithNullDescriptionDefaultsToEmpty() {
        Function fn = Function.create("my-func", null, MODULE, LIMITS);
        assertEquals("", fn.description());
    }
}
