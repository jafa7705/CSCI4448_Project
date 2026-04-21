package recipeRealm.service;

import recipeRealm.model.Ingredient;
import recipeRealm.repository.InventoryRepository;

import java.util.List;


public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public void addIngredient(Ingredient ingredient) {
        inventoryRepository.save(ingredient);
    }

    public void restockIngredient(String id, double amount) {
        Ingredient ingredient = inventoryRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Ingredient not found: " + id));
        ingredient.restock(amount);
        inventoryRepository.update(ingredient);
    }

    public List<Ingredient> purgeExpiredIngredients() {
        List<Ingredient> expired = inventoryRepository.findExpired();
        expired.forEach(i -> inventoryRepository.delete(i.getId()));
        return expired;
    }

    public List<Ingredient> getLowStockIngredients() {
        return inventoryRepository.findLowStock();
    }

    public List<Ingredient> getAllIngredients() {
        return inventoryRepository.findAll();
    }

    public boolean hasAllIngredients(List<Ingredient> required) {
        for (Ingredient req : required) {
            boolean inStock = inventoryRepository.findById(req.getId())
                    .map(Ingredient::isAvailable)
                    .orElse(false);
            if (!inStock) return false;
        }
        return true;
    }
}