package recipeRealm.observer;

import recipeRealm.model.CookingResult;
import recipeRealm.model.CustomerOrder;


public class SatisfactionObserver implements KitchenObserver {

    private int totalOrders = 0;
    private int totalSatisfaction = 0;

    @Override
    public void onDishCompleted(CustomerOrder order, CookingResult result) {
        totalOrders++;
        totalSatisfaction += order.getSatisfactionScore();
        System.out.printf("[Satisfaction] Average: %d%% over %d orders%n",
                getAverageSatisfaction(), totalOrders);
    }

    public int getAverageSatisfaction() {
        return totalOrders == 0 ? 0 : totalSatisfaction / totalOrders;
    }

    public int getTotalOrders() { return totalOrders; }
}