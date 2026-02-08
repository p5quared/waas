package net.peterv.registry.adapter.out.stub;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import net.peterv.common.types.FunctionId;
import net.peterv.registry.domain.model.Function;
import net.peterv.registry.domain.repository.FunctionRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@DefaultBean
@ApplicationScoped
public class InMemoryFunctionRepository implements FunctionRepository {

	private final ConcurrentHashMap<String, Function> store = new ConcurrentHashMap<>();

	@Override
	public void save(Function function) {
		store.put(function.id().value(), function);
	}

	@Override
	public Optional<Function> findById(FunctionId id) {
		return Optional.ofNullable(store.get(id.value()));
	}

	@Override
	public Optional<Function> findByName(String name) {
		return store.values().stream()
				.filter(f -> f.name().equals(name))
				.findFirst();
	}

	@Override
	public boolean existsByName(String name) {
		return store.values().stream()
				.anyMatch(f -> f.name().equals(name));
	}

	@Override
	public void delete(FunctionId id) {
		store.remove(id.value());
	}

	@Override
	public List<Function> listAll() {
		return List.copyOf(store.values());
	}

	public void clear() {
		store.clear();
	}
}
