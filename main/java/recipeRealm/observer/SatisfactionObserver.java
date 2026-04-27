package recipeRealm.observer;

import java.util.ArrayList;
import java.util.List;

import recipeRealm.model.CookingResult;
import recipeRealm.model.CustomerOrder;


public class SatisfactionObserver implements KitchenObserver {

    private int totalOrders = 0;
    private int totalSatisfaction = 0;
    private List<Integer> satisfactionScores = new ArrayList<>();

    @Override
    public void onDishCompleted(CustomerOrder order, CookingResult result) {
        totalOrders++;
        totalSatisfaction += order.getSatisfactionScore();
        satisfactionScores.add(order.getSatisfactionScore());
    }

    public int getAverageSatisfaction() {
        return totalOrders == 0 ? 0 : totalSatisfaction / totalOrders;
    }

    public int getTotalOrders() { return totalOrders; }

    public int getLastSatisfactionScore(){
        return satisfactionScores.getLast();
    }
}