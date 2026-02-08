package net.peterv.execution.adapter.out.stub;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.peterv.common.types.FunctionId;
import net.peterv.common.types.FunctionMetadata;
import net.peterv.execution.application.port.FunctionResolverPort;
import net.peterv.registry.application.service.FunctionRegistryService;
import net.peterv.registry.application.usecase.FunctionUseCases;
import net.peterv.registry.domain.exception.FunctionNotFoundException;

import java.util.Optional;

@DefaultBean
@ApplicationScoped
public class DirectFunctionResolver implements FunctionResolverPort {

    private final FunctionRegistryService registryService;

    @Inject
    public DirectFunctionResolver(FunctionRegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    public Optional<FunctionMetadata> resolve(FunctionId id) {
        try {
            return Optional.of(registryService.get(new FunctionUseCases.GetFunction(id)));
        } catch (FunctionNotFoundException e) {
            return Optional.empty();
        }
    }
}
