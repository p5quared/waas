package net.peterv.execution.adapter.out.stub;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.peterv.common.types.ModuleReference;
import net.peterv.execution.application.port.ModuleStoreReadPort;
import net.peterv.registry.adapter.out.stub.InMemoryModuleStore;

import java.util.Optional;

@DefaultBean
@ApplicationScoped
public class DirectModuleStoreReader implements ModuleStoreReadPort {

    private final InMemoryModuleStore moduleStore;

    @Inject
    public DirectModuleStoreReader(InMemoryModuleStore moduleStore) {
        this.moduleStore = moduleStore;
    }

    @Override
    public Optional<byte[]> fetch(ModuleReference ref) {
        return moduleStore.fetch(ref);
    }
}
