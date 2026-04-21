package recipeRealm.repository;

import recipeRealm.model.Recipe;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryRecipeRepository implements StorageRepository<Recipe, String> {

    private final Map<String, Recipe> store = new LinkedHashMap<>();

    @Override
    public void save(Recipe recipe) {
        Objects.requireNonNull(recipe, "Recipe must not be null");
        store.put(recipe.getId(), recipe);
    }

    @Override
    public Optional<Recipe> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Recipe> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(String id) {
        store.remove(id);
    }

    @Override
    public void update(Recipe recipe) {
        if (!store.containsKey(recipe.getId()))
            throw new NoSuchElementException("Recipe not found: " + recipe.getId());
        store.put(recipe.getId(), recipe);
    }

    public List<Recipe> findByCategory(String category) {
        return store.values().stream()
                .filter(r -> r.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Recipe> findByMaxSkillLevel(int playerSkill) {
        return store.values().stream()
                .filter(r -> r.getRequiredSkillLevel() <= playerSkill)
                .collect(Collectors.toList());
    }

    public int size() { return store.size(); }
}