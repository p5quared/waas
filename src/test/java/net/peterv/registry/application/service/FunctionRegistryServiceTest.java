package net.peterv.registry.application.service;

import net.peterv.common.events.DomainEvent;
import net.peterv.common.events.FunctionCreated;
import net.peterv.common.events.FunctionDeleted;
import net.peterv.common.events.FunctionUpdated;
import net.peterv.common.ports.EventPublisher;
import net.peterv.common.types.FunctionId;
import net.peterv.common.types.FunctionMetadata;
import net.peterv.common.types.ModuleReference;
import net.peterv.registry.application.port.ModuleStoreWritePort;
import net.peterv.registry.application.usecase.FunctionUseCases.*;
import net.peterv.registry.domain.exception.FunctionNotFoundException;
import net.peterv.registry.domain.model.Function;
import net.peterv.registry.domain.model.ResourceLimits;
import net.peterv.registry.domain.repository.FunctionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FunctionRegistryServiceTest {

    private FunctionRepository repository;
    private ModuleStoreWritePort moduleStore;
    private EventPublisher eventPublisher;
    private FunctionRegistryService service;

    private static final ModuleReference MODULE_REF = new ModuleReference("sha256:abc123");
    private static final byte[] WASM_BYTES = new byte[]{0x00, 0x61, 0x73, 0x6d};

    @BeforeEach
    void setUp() {
        repository = mock(FunctionRepository.class);
        moduleStore = mock(ModuleStoreWritePort.class);
        eventPublisher = mock(EventPublisher.class);
        service = new FunctionRegistryService(repository, moduleStore, eventPublisher);
    }

    @Test
    void createHappyPath() {
        when(repository.existsByName("my-func")).thenReturn(false);
        when(moduleStore.store(WASM_BYTES)).thenReturn(MODULE_REF);

        var cmd = new CreateFunction("my-func", "desc", WASM_BYTES, null);
        FunctionMetadata result = service.create(cmd);

        assertEquals("my-func", result.name());
        assertEquals(MODULE_REF, result.moduleReference());
        assertEquals(ResourceLimits.DEFAULTS.timeout(), result.timeout());
        assertEquals(ResourceLimits.DEFAULTS.memoryLimitBytes(), result.memoryLimitBytes());

        verify(repository).save(any(Function.class));

        ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertInstanceOf(FunctionCreated.class, eventCaptor.getValue());
    }

    @Test
    void updateWithNewBytesStoresModuleAndEmitsEvent() {
        ModuleReference oldRef = new ModuleReference("sha256:old");
        ModuleReference newRef = new ModuleReference("sha256:new");
        Function existing = Function.reconstitute(
                FunctionId.generate(), "my-func", "", oldRef,
                ResourceLimits.DEFAULTS,
                java.time.Instant.now(), java.time.Instant.now()
        );
        FunctionId id = existing.id();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(moduleStore.store(WASM_BYTES)).thenReturn(newRef);

        var cmd = new UpdateFunction(id, null, WASM_BYTES, null);
        FunctionMetadata result = service.update(cmd);

        assertEquals(newRef, result.moduleReference());
        verify(moduleStore).store(WASM_BYTES);
        verify(repository).save(existing);

        ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertInstanceOf(FunctionUpdated.class, eventCaptor.getValue());
    }

    @Test
    void updateWithoutBytesDoesNotCallModuleStore() {
        Function existing = Function.reconstitute(
                FunctionId.generate(), "my-func", "", MODULE_REF,
                ResourceLimits.DEFAULTS,
                java.time.Instant.now(), java.time.Instant.now()
        );
        FunctionId id = existing.id();

        when(repository.findById(id)).thenReturn(Optional.of(existing));

        var cmd = new UpdateFunction(id, "new desc", null, null);
        FunctionMetadata result = service.update(cmd);

        assertEquals("my-func", result.name());
        verify(moduleStore, never()).store(any());
        verify(repository).save(existing);
    }

    @Test
    void deletePublishesFunctionDeletedEvent() {
        FunctionId id = FunctionId.generate();
        Function existing = Function.reconstitute(
                id, "my-func", "", MODULE_REF,
                ResourceLimits.DEFAULTS,
                java.time.Instant.now(), java.time.Instant.now()
        );
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        service.delete(new DeleteFunction(id));

        verify(repository).delete(id);

        ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertInstanceOf(FunctionDeleted.class, eventCaptor.getValue());
        assertEquals(id, eventCaptor.getValue().functionId());
    }

    @Test
    void getNonexistentThrowsFunctionNotFoundException() {
        FunctionId id = FunctionId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(FunctionNotFoundException.class,
                () -> service.get(new GetFunction(id)));
    }

    @Test
    void listReturnsMetadata() {
        Function fn1 = Function.reconstitute(
                FunctionId.generate(), "func-a", "", MODULE_REF,
                ResourceLimits.DEFAULTS,
                java.time.Instant.now(), java.time.Instant.now()
        );
        Function fn2 = Function.reconstitute(
                FunctionId.generate(), "func-b", "", MODULE_REF,
                ResourceLimits.DEFAULTS,
                java.time.Instant.now(), java.time.Instant.now()
        );
        when(repository.listAll()).thenReturn(List.of(fn1, fn2));

        List<FunctionMetadata> results = service.list(new ListFunctions());

        assertEquals(2, results.size());
        assertEquals("func-a", results.get(0).name());
        assertEquals("func-b", results.get(1).name());
    }
}
