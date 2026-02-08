package net.peterv.execution.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionContextTest {

    @Test
    void validContextIsCreated() {
        var ctx = new ExecutionContext(Duration.ofSeconds(30), 1024, Map.of("KEY", "VAL"));
        assertEquals(Duration.ofSeconds(30), ctx.timeout());
        assertEquals(1024, ctx.memoryLimitBytes());
        assertEquals(Map.of("KEY", "VAL"), ctx.environmentVariables());
    }

    @Test
    void nullTimeoutThrows() {
        assertThrows(NullPointerException.class,
                () -> new ExecutionContext(null, 1024, Map.of()));
    }

    @Test
    void zeroMemoryLimitThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExecutionContext(Duration.ofSeconds(1), 0, Map.of()));
    }

    @Test
    void negativeMemoryLimitThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExecutionContext(Duration.ofSeconds(1), -1, Map.of()));
    }

    @Test
    void nullEnvVarsDefaultsToEmptyMap() {
        var ctx = new ExecutionContext(Duration.ofSeconds(1), 1024, null);
        assertEquals(Map.of(), ctx.environmentVariables());
    }
}
