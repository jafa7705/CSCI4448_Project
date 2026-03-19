package recipeRealm.observer;

import recipeRealm.model.CookingResult;
import recipeRealm.model.CustomerOrder;

public class OrderFulfillmentObserver implements KitchenObserver {

    private double totalEarnings = 0.0;

    @Override
    public void onDishCompleted(CustomerOrder order, CookingResult result) {
        order.complete(result);

        if (order.getStatus() == CustomerOrder.Status.COMPLETED) {
            double basePrice = order.getRequestedRecipe().getBasePrice();
            double ratio = Math.max(0.2, result.getScore() / 100.0);
            double earned = basePrice * ratio;
            totalEarnings += earned;
            System.out.printf("[Order] %s completed. Earned $%.2f (score %d)%n",
                    order.getOrderId(), earned, result.getScore());
        } else {
            System.out.printf("[Order] %s failed/expired. No earnings.%n", order.getOrderId());
        }
    }

    public double getTotalEarnings() { return totalEarnings; }
}



class SatisfactionObserver implements KitchenObserver {

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
