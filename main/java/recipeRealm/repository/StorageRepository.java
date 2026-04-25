package recipeRealm.repository;

import java.util.List;
import java.util.Optional;

public interface StorageRepository<ID, T> {
    void save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(ID id);
    void update(T entity);
}