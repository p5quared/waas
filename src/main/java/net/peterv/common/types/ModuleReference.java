package net.peterv.common.types;

import java.util.Objects;

public record ModuleReference(String contentHash) {

    public ModuleReference {
        Objects.requireNonNull(contentHash, "Content hash must not be null");
    }
}
