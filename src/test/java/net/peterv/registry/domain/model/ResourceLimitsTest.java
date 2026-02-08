package net.peterv.registry.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ResourceLimitsTest {

    @Test
    void defaultsAreSane() {
        assertEquals(Duration.ofSeconds(30), ResourceLimits.DEFAULTS.timeout());
        assertEquals(64 * 1024 * 1024, ResourceLimits.DEFAULTS.memoryLimitBytes());
    }

    @Test
    void negativeTimeoutRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResourceLimits(Duration.ofSeconds(-1), 1024));
    }

    @Test
    void zeroMemoryRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResourceLimits(Duration.ofSeconds(10), 0));
    }

    @Test
    void negativeMemoryRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResourceLimits(Duration.ofSeconds(10), -1));
    }

    @Test
    void validConstructionWorks() {
        var limits = new ResourceLimits(Duration.ofSeconds(5), 1024);
        assertEquals(Duration.ofSeconds(5), limits.timeout());
        assertEquals(1024, limits.memoryLimitBytes());
    }
}
