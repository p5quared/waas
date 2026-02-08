package net.peterv.registry.domain.exception;

import net.peterv.common.types.FunctionId;

public class FunctionNotFoundException extends RuntimeException {

    public FunctionNotFoundException(FunctionId id) {
        super("Function not found: " + id.value());
    }

    public FunctionNotFoundException(String name) {
        super("Function not found: " + name);
    }
}
