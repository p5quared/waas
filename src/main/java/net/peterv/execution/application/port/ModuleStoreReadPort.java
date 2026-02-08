package net.peterv.execution.application.port;

import net.peterv.common.types.ModuleReference;

import java.util.Optional;

public interface ModuleStoreReadPort {

    Optional<byte[]> fetch(ModuleReference ref);
}
