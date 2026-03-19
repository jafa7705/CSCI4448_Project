package recipeRealm.observer;

import recipeRealm.model.CookingResult;
import recipeRealm.model.CustomerOrder;
import java.util.ArrayList;
import java.util.List;


public class KitchenEventPublisher {

    private final List<KitchenObserver> observers = new ArrayList<>();

    public void registerObserver(KitchenObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(KitchenObserver observer) {
        observers.remove(observer);
    }

    public void notifyDishCompleted(CustomerOrder order, CookingResult result) {
        for (KitchenObserver observer : observers) {
            observer.onDishCompleted(order, result);
        }
    }
}
