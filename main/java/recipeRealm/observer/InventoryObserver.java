package recipeRealm.observer;

import recipeRealm.model.Ingredient;


public interface InventoryObserver {
    void onLowStock(Ingredient ingredient, double currentQuantity);
    void onStockExpired(Ingredient ingredient);
}
