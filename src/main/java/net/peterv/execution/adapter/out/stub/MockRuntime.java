package net.peterv.execution.adapter.out.stub;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import net.peterv.execution.domain.model.ExecutionContext;
import net.peterv.execution.domain.model.InvocationResult;
import net.peterv.execution.domain.runtime.WasmRuntime;

import java.time.Duration;

@DefaultBean
@ApplicationScoped
public class MockRuntime implements WasmRuntime {

    private byte[] lastModuleBytes;
    private ExecutionContext lastContext;
    private byte[] lastStdin;
    private InvocationResult cannedResult = new InvocationResult(0, new byte[0], new byte[0], Duration.ofMillis(1));

    @Override
    public InvocationResult execute(byte[] moduleBytes, ExecutionContext context, byte[] stdin) {
        this.lastModuleBytes = moduleBytes;
        this.lastContext = context;
        this.lastStdin = stdin;
        return cannedResult;
    }

    public void setCannedResult(InvocationResult result) {
        this.cannedResult = result;
    }

    public byte[] getLastModuleBytes() { return lastModuleBytes; }
    public ExecutionContext getLastContext() { return lastContext; }
    public byte[] getLastStdin() { return lastStdin; }
}
