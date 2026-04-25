package recipeRealm.observer;
import java.util.ArrayList;
import java.util.List;

import recipeRealm.model.Ingredient;
public class StockAlertObserver implements InventoryObserver{

    private final List<Ingredient> alerts = new ArrayList<>();

    @Override
    public void onLowStock(Ingredient ingredient, double currentQuantity) {
        alerts.add(ingredient);
    };

    @Override
    public void onStockExpired(Ingredient ingredient) {
        alerts.add(ingredient);
    };
    public List<Ingredient> getAlerts(){
        return alerts;
    }

    public void clearAlerts(){
        alerts.clear();
    }
    
}
