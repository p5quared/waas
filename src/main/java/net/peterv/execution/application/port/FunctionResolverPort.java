package net.peterv.execution.application.port;

import net.peterv.common.types.FunctionId;
import net.peterv.common.types.FunctionMetadata;

import java.util.Optional;

public interface FunctionResolverPort {

    Optional<FunctionMetadata> resolve(FunctionId id);
}
