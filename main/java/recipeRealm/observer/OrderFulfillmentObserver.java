package recipeRealm.observer;

import recipeRealm.model.CookingResult;
import recipeRealm.model.CustomerOrder;

import java.util.ArrayList;
import java.util.List;


public class OrderFulfillmentObserver implements KitchenObserver {

    private double totalEarnings = 0.0;
    private final List<Double> earnedPerOrder = new ArrayList<>();

    @Override
    public void onDishCompleted(CustomerOrder order, CookingResult result) {
        order.complete(result);

        if (order.getStatus() == CustomerOrder.Status.COMPLETED) {
            double basePrice = order.getRequestedRecipe().getBasePrice();
            double ratio = Math.max(0.2, result.getScore() / 100.0);
            double earned = basePrice * ratio;
            totalEarnings += earned;
            earnedPerOrder.add(earned);
        }else{
            earnedPerOrder.add(0.0);
        }
    }

    public double getTotalEarnings() { return totalEarnings; }

    public void spend(double amount){
        totalEarnings -= amount;
    }

    public double getLastEarned(){
        return earnedPerOrder.getLast();
    }
}