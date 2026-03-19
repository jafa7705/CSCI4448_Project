package recipeRealm.repository;

import recipeRealm.model.Ingredient;
import recipeRealm.observer.InventoryObserver;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryRepository implements StorageRepository<Ingredient, String> {

    private static final double LOW_STOCK_MULTIPLIER = 2.0; // warn if qty < 2× required

    private final Map<String, Ingredient> store = new LinkedHashMap<>();
    private final List<InventoryObserver> observers = new ArrayList<>();

    public void addObserver(InventoryObserver observer) { observers.add(observer); }
    public void removeObserver(InventoryObserver observer) { observers.remove(observer); }

    @Override
    public void save(Ingredient ingredient) {
        store.put(ingredient.getId(), ingredient);
    }

    @Override
    public Optional<Ingredient> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Ingredient> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(String id) { store.remove(id); }

    @Override
    public void update(Ingredient ingredient) {
        store.put(ingredient.getId(), ingredient);
        checkThresholds(ingredient);
    }

    public void consumeIngredients(List<Ingredient> required) {
        for (Ingredient req : required) {
            Ingredient stock = store.get(req.getId());
            if (stock == null) throw new NoSuchElementException("Ingredient not in inventory: " + req.getName());
            if (stock.isExpired()) {
                notifyExpired(stock);
                throw new IllegalStateException("Ingredient expired: " + stock.getName());
            }
            stock.consume();
            checkThresholds(stock);
        }
    }

    public List<Ingredient> findExpired() {
        return store.values().stream()
                .filter(Ingredient::isExpired)
                .collect(Collectors.toList());
    }

    public List<Ingredient> findLowStock() {
        return store.values().stream()
                .filter(i -> !i.isExpired() && i.getQuantity() < i.getRequiredAmount() * LOW_STOCK_MULTIPLIER)
                .collect(Collectors.toList());
    }

    private void checkThresholds(Ingredient ingredient) {
        if (ingredient.isExpired()) {
            notifyExpired(ingredient);
        } else if (ingredient.getQuantity() < ingredient.getRequiredAmount() * LOW_STOCK_MULTIPLIER) {
            observers.forEach(obs -> obs.onLowStock(ingredient, ingredient.getQuantity()));
        }
    }

    private void notifyExpired(Ingredient ingredient) {
        observers.forEach(obs -> obs.onStockExpired(ingredient));
    }
}
