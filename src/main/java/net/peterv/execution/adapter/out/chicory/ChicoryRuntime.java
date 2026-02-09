package net.peterv.execution.adapter.out.chicory;

import com.dylibso.chicory.runtime.Store;
import com.dylibso.chicory.wasi.WasiExitException;
import com.dylibso.chicory.wasi.WasiOptions;
import com.dylibso.chicory.wasi.WasiPreview1;
import com.dylibso.chicory.wasm.Parser;
import jakarta.enterprise.context.ApplicationScoped;
import net.peterv.execution.domain.exception.ModuleInstantiationException;
import net.peterv.execution.domain.model.ExecutionContext;
import net.peterv.execution.domain.model.InvocationResult;
import net.peterv.execution.domain.runtime.WasmRuntime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class ChicoryRuntime implements WasmRuntime {

    @Override
    public InvocationResult execute(byte[] moduleBytes, ExecutionContext context, byte[] stdin) {
        var stdinStream = new ByteArrayInputStream(stdin != null ? stdin : new byte[0]);
        var stdoutStream = new ByteArrayOutputStream();
        var stderrStream = new ByteArrayOutputStream();

        var optionsBuilder = WasiOptions.builder()
                .withStdin(stdinStream)
                .withStdout(stdoutStream)
                .withStderr(stderrStream);

        for (var entry : context.environmentVariables().entrySet()) {
            optionsBuilder.withEnvironment(entry.getKey(), entry.getValue());
        }

        var wasi = WasiPreview1.builder()
                .withOptions(optionsBuilder.build())
                .build();

        var start = Instant.now();
        int exitCode = 0;

        try {
            var module = Parser.parse(moduleBytes);
            var store = new Store().addFunction(wasi.toHostFunctions());
            store.instantiate("module", module);
        } catch (WasiExitException e) {
            exitCode = e.exitCode();
        } catch (Exception e) {
            throw new ModuleInstantiationException("Failed to execute WebAssembly module", e);
        }

        var duration = Duration.between(start, Instant.now());
        return new InvocationResult(exitCode, stdoutStream.toByteArray(), stderrStream.toByteArray(), duration);
    }
}
