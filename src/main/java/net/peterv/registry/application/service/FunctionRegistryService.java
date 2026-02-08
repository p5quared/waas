package net.peterv.registry.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.peterv.common.events.DomainEvent;
import net.peterv.common.events.FunctionDeleted;
import net.peterv.common.ports.EventPublisher;
import net.peterv.common.types.FunctionMetadata;
import net.peterv.common.types.ModuleReference;
import net.peterv.registry.application.port.ModuleStoreWritePort;
import net.peterv.registry.application.usecase.FunctionUseCases.CreateFunction;
import net.peterv.registry.application.usecase.FunctionUseCases.DeleteFunction;
import net.peterv.registry.application.usecase.FunctionUseCases.GetFunction;
import net.peterv.registry.application.usecase.FunctionUseCases.ListFunctions;
import net.peterv.registry.application.usecase.FunctionUseCases.UpdateFunction;
import net.peterv.registry.domain.exception.FunctionNotFoundException;
import net.peterv.registry.domain.model.Function;
import net.peterv.registry.domain.model.ResourceLimits;
import net.peterv.registry.domain.repository.FunctionRepository;

import java.util.List;

@ApplicationScoped
public class FunctionRegistryService {

    private final FunctionRepository repository;
    private final ModuleStoreWritePort moduleStore;
    private final EventPublisher eventPublisher;

    @Inject
    public FunctionRegistryService(
            FunctionRepository repository,
            ModuleStoreWritePort moduleStore,
            EventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.moduleStore = moduleStore;
        this.eventPublisher = eventPublisher;
    }

    public FunctionMetadata create(CreateFunction cmd) {
        ModuleReference moduleRef = moduleStore.store(cmd.wasmBytes());

        ResourceLimits limits = cmd.resourceLimits() != null
                ? cmd.resourceLimits()
                : ResourceLimits.DEFAULTS;

        Function function = Function.create(
                cmd.name(),
                cmd.description(),
                moduleRef,
                limits
        );

        repository.save(function);
        publishEvents(function);

        return function.toMetadata();
    }

    public FunctionMetadata update(UpdateFunction cmd) {
        Function function = repository.findById(cmd.id())
                .orElseThrow(() -> new FunctionNotFoundException(cmd.id()));

        if (cmd.wasmBytes() != null) {
            ModuleReference newRef = moduleStore.store(cmd.wasmBytes());
            function.updateModule(newRef);
        }

        function.updateConfiguration(
                cmd.description(),
                cmd.resourceLimits()
        );

        repository.save(function);
        publishEvents(function);

        return function.toMetadata();
    }

    public void delete(DeleteFunction cmd) {
        if (repository.findById(cmd.id()).isEmpty()) {
            throw new FunctionNotFoundException(cmd.id());
        }

        repository.delete(cmd.id());
        eventPublisher.publish(FunctionDeleted.now(cmd.id()));
    }

    public FunctionMetadata get(GetFunction query) {
        return repository.findById(query.id())
                .map(Function::toMetadata)
                .orElseThrow(() -> new FunctionNotFoundException(query.id()));
    }

    public List<FunctionMetadata> list(ListFunctions ignoredQuery) {
        return repository.listAll().stream()
                .map(Function::toMetadata)
                .toList();
    }

    private void publishEvents(Function function) {
        for (DomainEvent event : function.drainEvents()) {
            eventPublisher.publish(event);
        }
    }
}
