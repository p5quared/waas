package net.peterv.registry.adapter.out.stub;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import net.peterv.common.types.ModuleReference;
import net.peterv.registry.application.port.ModuleStoreWritePort;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;

@DefaultBean
@ApplicationScoped
public class InMemoryModuleStore implements ModuleStoreWritePort {

    private final ConcurrentHashMap<String, byte[]> store = new ConcurrentHashMap<>();

    @Override
    public ModuleReference store(byte[] wasmBytes) {
        String hash = sha256(wasmBytes);
        store.put(hash, wasmBytes);
        return new ModuleReference(hash);
    }

    private static String sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
