package net.peterv.common.types;

import java.util.Objects;
import java.util.UUID;

public record FunctionId(String value) {

    public FunctionId {
        Objects.requireNonNull(value, "FunctionId value must not be null");
    }

    public static FunctionId generate() {
        return new FunctionId(UUID.randomUUID().toString());
    }
}
