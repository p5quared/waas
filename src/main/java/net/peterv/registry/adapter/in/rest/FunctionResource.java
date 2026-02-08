package net.peterv.registry.adapter.in.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.peterv.common.types.FunctionId;
import net.peterv.common.types.FunctionMetadata;
import net.peterv.registry.adapter.in.rest.dto.CreateFunctionRequest;
import net.peterv.registry.adapter.in.rest.dto.FunctionResponse;
import net.peterv.registry.adapter.in.rest.dto.UpdateFunctionRequest;
import net.peterv.registry.adapter.in.rest.mapper.FunctionDtoMapper;
import net.peterv.registry.application.service.FunctionRegistryService;
import net.peterv.registry.application.usecase.FunctionUseCases;

import java.net.URI;
import java.util.List;

@Path("/api/v1/functions")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FunctionResource {

    private final FunctionRegistryService service;
    private final FunctionDtoMapper mapper;

    @Inject
    public FunctionResource(FunctionRegistryService service, FunctionDtoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @POST
    public Response create(CreateFunctionRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Function name is required");
        }
        if (request.wasmBytes() == null || request.wasmBytes().length == 0) {
            throw new IllegalArgumentException("WASM bytes are required");
        }

        FunctionMetadata metadata = service.create(mapper.toCreateCommand(request));
        FunctionResponse response = mapper.toResponse(metadata);

        return Response.status(Response.Status.CREATED)
                .location(URI.create("/api/v1/functions/" + response.id()))
                .entity(response)
                .build();
    }

    @GET
    @Path("/{id}")
    public FunctionResponse get(@PathParam("id") String id) {
        FunctionMetadata metadata = service.get(new FunctionUseCases.GetFunction(new FunctionId(id)));
        return mapper.toResponse(metadata);
    }

    @GET
    public List<FunctionResponse> list() {
        return service.list(new FunctionUseCases.ListFunctions()).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PUT
    @Path("/{id}")
    public FunctionResponse update(@PathParam("id") String id, UpdateFunctionRequest request) {
        FunctionMetadata metadata = service.update(mapper.toUpdateCommand(id, request));
        return mapper.toResponse(metadata);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        service.delete(new FunctionUseCases.DeleteFunction(new FunctionId(id)));
        return Response.noContent().build();
    }
}
