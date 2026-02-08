package net.peterv.common.types;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FunctionIdTest {

    @Test
    void generateProducesValidUUID() {
        FunctionId id = FunctionId.generate();
        assertDoesNotThrow(() -> UUID.fromString(id.value()));
    }

    @Test
    void generateProducesUniqueIds() {
        FunctionId id1 = FunctionId.generate();
        FunctionId id2 = FunctionId.generate();
        assertNotEquals(id1, id2);
    }

    @Test
    void equalityBasedOnValue() {
        String uuid = UUID.randomUUID().toString();
        FunctionId id1 = new FunctionId(uuid);
        FunctionId id2 = new FunctionId(uuid);
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void nullValueRejected() {
        assertThrows(NullPointerException.class, () -> new FunctionId(null));
    }
}
