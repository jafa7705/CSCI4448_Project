package recipeRealm.observer;

import recipeRealm.model.CookingResult;
import recipeRealm.model.CustomerOrder;


public interface KitchenObserver {
    void onDishCompleted(CustomerOrder order, CookingResult result);
}
