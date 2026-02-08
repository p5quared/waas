package net.peterv.execution.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class InvocationResultTest {

    @Test
    void succeededReturnsTrueForExitCodeZero() {
        var result = new InvocationResult(0, new byte[0], new byte[0], Duration.ofMillis(1));
        assertTrue(result.succeeded());
    }

    @Test
    void succeededReturnsFalseForNonZeroExitCode() {
        var result = new InvocationResult(1, new byte[0], new byte[0], Duration.ofMillis(1));
        assertFalse(result.succeeded());
    }

    @Test
    void outputReturnsEmptyWhenStdoutIsEmpty() {
        var result = new InvocationResult(0, new byte[0], new byte[0], Duration.ofMillis(1));
        assertTrue(result.output().isEmpty());
    }

    @Test
    void outputReturnsBytesWhenStdoutHasContent() {
        byte[] data = "hello".getBytes();
        var result = new InvocationResult(0, data, new byte[0], Duration.ofMillis(1));
        assertTrue(result.output().isPresent());
        assertArrayEquals(data, result.output().get());
    }

    @Test
    void stderrAsStringReturnsDecodedStderr() {
        var result = new InvocationResult(1, new byte[0], "error msg".getBytes(), Duration.ofMillis(1));
        assertEquals("error msg", result.stderrAsString());
    }

    @Test
    void stderrAsStringReturnsEmptyForNullStderr() {
        var result = new InvocationResult(0, new byte[0], null, Duration.ofMillis(1));
        assertEquals("", result.stderrAsString());
    }
}
