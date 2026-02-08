package net.peterv.execution.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.peterv.common.types.FunctionMetadata;
import net.peterv.execution.application.port.FunctionResolverPort;
import net.peterv.execution.application.port.ModuleStoreReadPort;
import net.peterv.execution.application.usecase.InvokeFunction;
import net.peterv.execution.domain.model.ExecutionContext;
import net.peterv.execution.domain.model.InvocationResult;
import net.peterv.execution.domain.runtime.WasmRuntime;

import java.util.Map;

@ApplicationScoped
public class InvocationService {

    private final FunctionResolverPort functionResolver;
    private final ModuleStoreReadPort moduleStore;
    private final WasmRuntime runtime;

    @Inject
    public InvocationService(
            FunctionResolverPort functionResolver,
            ModuleStoreReadPort moduleStore,
            WasmRuntime runtime
    ) {
        this.functionResolver = functionResolver;
        this.moduleStore = moduleStore;
        this.runtime = runtime;
    }

    public InvocationResult invoke(InvokeFunction command) {
        FunctionMetadata metadata = functionResolver.resolve(command.functionId())
                .orElseThrow(() -> new FunctionNotFoundException(command.functionId().value()));

        byte[] moduleBytes = moduleStore.fetch(metadata.moduleReference())
                .orElseThrow(() -> new ModuleNotFoundException(metadata.moduleReference().contentHash()));

        ExecutionContext context = new ExecutionContext(
                metadata.timeout(),
                metadata.memoryLimitBytes(),
                Map.of()
        );

        return runtime.execute(moduleBytes, context, command.stdin());
    }

    public static class FunctionNotFoundException extends RuntimeException {
        public FunctionNotFoundException(String functionId) {
            super("Function not found: " + functionId);
        }
    }

    public static class ModuleNotFoundException extends RuntimeException {
        public ModuleNotFoundException(String contentHash) {
            super("Module not found: " + contentHash);
        }
    }
}
