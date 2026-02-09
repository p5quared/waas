package net.peterv.execution.adapter.out.chicory;

import net.peterv.execution.domain.exception.ModuleInstantiationException;
import net.peterv.execution.domain.model.ExecutionContext;
import net.peterv.execution.domain.model.InvocationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChicoryRuntimeTest {

    private ChicoryRuntime runtime;
    private ExecutionContext defaultContext;

    @BeforeEach
    void setUp() {
        runtime = new ChicoryRuntime();
        defaultContext = new ExecutionContext(Duration.ofSeconds(10), 64 * 1024 * 1024, Map.of());
    }

    @Test
    void invalidWasmBytesThrowsModuleInstantiationException() {
        byte[] garbage = new byte[]{0x00, 0x01, 0x02, 0x03};
        assertThrows(ModuleInstantiationException.class,
                () -> runtime.execute(garbage, defaultContext, new byte[0]));
    }

    // WASI binaries in src/test/resources/wasm/:
    @Test
    void helloModuleWritesToStdout() throws IOException {
        byte[] module = loadResource("/wasm/stdecho.wasm");
        String echoInput = "echo";
        InvocationResult result = runtime.execute(module, defaultContext, echoInput.getBytes());

        assertEquals(0, result.exitCode());
        assertTrue(new String(result.stdout()).contains(echoInput));
        assertNotNull(result.executionTime());
    }

    @Test
    void nonZeroExitCodeCaptured() throws IOException {
        byte[] module = loadResource("/wasm/exit42.wasm");
        InvocationResult result = runtime.execute(module, defaultContext, new byte[0]);

        assertEquals(42, result.exitCode());
    }

    private byte[] loadResource(String path) throws IOException {
        try (var is = getClass().getResourceAsStream(path)) {
            assertNotNull(is, "Test resource not found: " + path);
            return is.readAllBytes();
        }
    }
}
