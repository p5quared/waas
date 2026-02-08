package net.peterv.registry.application.port;

import net.peterv.common.types.ModuleReference;

public interface ModuleStoreWritePort {

    ModuleReference store(byte[] wasmBytes);
}
