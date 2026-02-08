package net.peterv.execution.adapter.in.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.peterv.common.types.FunctionId;
import net.peterv.execution.application.service.InvocationService;
import net.peterv.execution.application.usecase.InvokeFunction;
import net.peterv.execution.domain.exception.ModuleInstantiationException;
import net.peterv.execution.domain.model.InvocationResult;

@Path("/invoke")
public class InvokeResource {

    private final InvocationService invocationService;

    @Inject
    public InvokeResource(InvocationService invocationService) {
        this.invocationService = invocationService;
    }

    @POST
    @Path("/{functionId}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response invoke(@PathParam("functionId") String functionId, byte[] stdin) {
        try {
            InvocationResult result = invocationService.invoke(
                    new InvokeFunction(new FunctionId(functionId), stdin)
            );

            return Response.ok(result.stdout())
                    .header("X-Exit-Code", result.exitCode())
                    .header("X-Execution-Time-Ms", result.executionTime().toMillis())
                    .build();
        } catch (InvocationService.FunctionNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage().getBytes())
                    .build();
        } catch (InvocationService.ModuleNotFoundException e) {
            return Response.status(502)
                    .entity(e.getMessage().getBytes())
                    .build();
        } catch (ModuleInstantiationException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage().getBytes())
                    .build();
        }
    }
}
