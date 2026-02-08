package net.peterv.execution.application.service;

import net.peterv.common.types.FunctionId;
import net.peterv.common.types.FunctionMetadata;
import net.peterv.common.types.ModuleReference;
import net.peterv.execution.application.port.FunctionResolverPort;
import net.peterv.execution.application.port.ModuleStoreReadPort;
import net.peterv.execution.application.usecase.InvokeFunction;
import net.peterv.execution.domain.exception.ModuleInstantiationException;
import net.peterv.execution.domain.model.ExecutionContext;
import net.peterv.execution.domain.model.InvocationResult;
import net.peterv.execution.domain.runtime.WasmRuntime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvocationServiceTest {

    private FunctionResolverPort functionResolver;
    private ModuleStoreReadPort moduleStore;
    private WasmRuntime runtime;
    private InvocationService service;

    private static final FunctionId FUNC_ID = FunctionId.generate();
    private static final ModuleReference MODULE_REF = new ModuleReference("sha256:abc123");
    private static final byte[] MODULE_BYTES = new byte[]{0x00, 0x61, 0x73, 0x6d};
    private static final byte[] STDIN = "input".getBytes();
    private static final FunctionMetadata METADATA = new FunctionMetadata(
            FUNC_ID, "test-func", MODULE_REF, Duration.ofSeconds(30), 64 * 1024 * 1024
    );

    @BeforeEach
    void setUp() {
        functionResolver = mock(FunctionResolverPort.class);
        moduleStore = mock(ModuleStoreReadPort.class);
        runtime = mock(WasmRuntime.class);
        service = new InvocationService(functionResolver, moduleStore, runtime);
    }

    @Test
    void happyPath() {
        when(functionResolver.resolve(FUNC_ID)).thenReturn(Optional.of(METADATA));
        when(moduleStore.fetch(MODULE_REF)).thenReturn(Optional.of(MODULE_BYTES));
        var expected = new InvocationResult(0, "out".getBytes(), new byte[0], Duration.ofMillis(5));
        when(runtime.execute(eq(MODULE_BYTES), any(ExecutionContext.class), eq(STDIN))).thenReturn(expected);

        InvocationResult result = service.invoke(new InvokeFunction(FUNC_ID, STDIN));

        assertEquals(0, result.exitCode());
        assertArrayEquals("out".getBytes(), result.stdout());
    }

    @Test
    void functionNotFoundThrows() {
        when(functionResolver.resolve(FUNC_ID)).thenReturn(Optional.empty());

        assertThrows(InvocationService.FunctionNotFoundException.class,
                () -> service.invoke(new InvokeFunction(FUNC_ID, STDIN)));
    }

    @Test
    void moduleNotFoundThrows() {
        when(functionResolver.resolve(FUNC_ID)).thenReturn(Optional.of(METADATA));
        when(moduleStore.fetch(MODULE_REF)).thenReturn(Optional.empty());

        assertThrows(InvocationService.ModuleNotFoundException.class,
                () -> service.invoke(new InvokeFunction(FUNC_ID, STDIN)));
    }

    @Test
    void runtimeExceptionPropagates() {
        when(functionResolver.resolve(FUNC_ID)).thenReturn(Optional.of(METADATA));
        when(moduleStore.fetch(MODULE_REF)).thenReturn(Optional.of(MODULE_BYTES));
        when(runtime.execute(any(), any(), any())).thenThrow(new ModuleInstantiationException("fail"));

        assertThrows(ModuleInstantiationException.class,
                () -> service.invoke(new InvokeFunction(FUNC_ID, STDIN)));
    }

    @Test
    void nonZeroExitCodeReturnedAsIs() {
        when(functionResolver.resolve(FUNC_ID)).thenReturn(Optional.of(METADATA));
        when(moduleStore.fetch(MODULE_REF)).thenReturn(Optional.of(MODULE_BYTES));
        var expected = new InvocationResult(42, new byte[0], "err".getBytes(), Duration.ofMillis(5));
        when(runtime.execute(any(), any(), any())).thenReturn(expected);

        InvocationResult result = service.invoke(new InvokeFunction(FUNC_ID, STDIN));

        assertEquals(42, result.exitCode());
        assertFalse(result.succeeded());
    }

    @Test
    void nullStdinNormalizedToEmpty() {
        var cmd = new InvokeFunction(FUNC_ID, null);
        assertNotNull(cmd.stdin());
        assertEquals(0, cmd.stdin().length);
    }
}
