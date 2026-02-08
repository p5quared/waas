package net.peterv.execution.domain.runtime;

import net.peterv.execution.domain.model.ExecutionContext;
import net.peterv.execution.domain.model.InvocationResult;

public interface WasmRuntime {

    InvocationResult execute(byte[] moduleBytes, ExecutionContext context, byte[] stdin);
}
