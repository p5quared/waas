package net.peterv.registry.adapter.in.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import net.peterv.common.types.FunctionId;
import net.peterv.common.types.FunctionMetadata;
import net.peterv.registry.adapter.in.rest.dto.CreateFunctionRequest;
import net.peterv.registry.adapter.in.rest.dto.FunctionResponse;
import net.peterv.registry.adapter.in.rest.dto.UpdateFunctionRequest;
import net.peterv.registry.application.usecase.FunctionUseCases.CreateFunction;
import net.peterv.registry.application.usecase.FunctionUseCases.UpdateFunction;
import net.peterv.registry.domain.model.ResourceLimits;

import java.time.Duration;

@ApplicationScoped
public class FunctionDtoMapper {

    public CreateFunction toCreateCommand(CreateFunctionRequest request) {
        return new CreateFunction(
                request.name(),
                request.description(),
                request.wasmBytes(),
                toResourceLimits(request.resourceLimits())
        );
    }

    public UpdateFunction toUpdateCommand(String id, UpdateFunctionRequest request) {
        return new UpdateFunction(
                new FunctionId(id),
                request.description(),
                request.wasmBytes(),
                toResourceLimits(request.resourceLimits())
        );
    }

    public FunctionResponse toResponse(FunctionMetadata metadata) {
        return new FunctionResponse(
                metadata.id().value(),
                metadata.name(),
                metadata.moduleReference().contentHash(),
                metadata.timeout().getSeconds(),
                metadata.memoryLimitBytes()
        );
    }

    private ResourceLimits toResourceLimits(net.peterv.registry.adapter.in.rest.dto.ResourceLimitsDto dto) {
        if (dto == null) {
            return null;
        }
        return new ResourceLimits(
                Duration.ofSeconds(dto.timeoutSeconds()),
                dto.memoryLimitBytes()
        );
    }
}
