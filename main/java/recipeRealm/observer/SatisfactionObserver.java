package recipeRealm.observer;

import recipeRealm.model.CookingResult;
import recipeRealm.model.CustomerOrder;

/**
 * Observer Pattern: Tracks average customer satisfaction across all orders.
 *
 * Satisfaction is computed by CustomerOrder.complete() as a blend of cooking
 * score (70%) and order timeliness (30%). This observer accumulates those
 * scores and reports a running average after every dish is served.
 */
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