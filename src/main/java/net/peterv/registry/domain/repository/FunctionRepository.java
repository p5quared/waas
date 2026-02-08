package net.peterv.registry.domain.repository;

import net.peterv.common.types.FunctionId;
import net.peterv.registry.domain.model.Function;

import java.util.List;
import java.util.Optional;

public interface FunctionRepository {

    void save(Function function);

    Optional<Function> findById(FunctionId id);

    Optional<Function> findByName(String name);

    boolean existsByName(String name);

    void delete(FunctionId id);

    List<Function> listAll();
}
