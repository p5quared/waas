package net.peterv.execution.domain.model;

import java.time.Duration;
import java.util.Optional;

public record InvocationResult(
        int exitCode,
        byte[] stdout,
        byte[] stderr,
        Duration executionTime
) {

    public boolean succeeded() {
        return exitCode == 0;
    }

    public Optional<byte[]> output() {
        return stdout != null && stdout.length > 0 ? Optional.of(stdout) : Optional.empty();
    }

    public String stderrAsString() {
        return stderr != null ? new String(stderr) : "";
    }
}
