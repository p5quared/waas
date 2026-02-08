package net.peterv.registry.adapter.in.rest.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import net.peterv.registry.domain.exception.FunctionNotFoundException;

import java.util.Map;

@Provider
public class FunctionNotFoundExceptionMapper implements ExceptionMapper<FunctionNotFoundException> {

    @Override
    public Response toResponse(FunctionNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", exception.getMessage()))
                .build();
    }
}
