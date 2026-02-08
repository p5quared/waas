package net.peterv.execution.application.usecase;

import net.peterv.common.types.FunctionId;

import java.util.Objects;

public record InvokeFunction(FunctionId functionId, byte[] stdin) {

    public InvokeFunction {
        Objects.requireNonNull(functionId, "functionId must not be null");
        if (stdin == null) {
            stdin = new byte[0];
        }
    }
}
