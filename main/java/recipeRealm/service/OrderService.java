package recipeRealm.service;

import recipeRealm.model.CustomerOrder;
import recipeRealm.model.CookingResult;
import recipeRealm.model.Recipe;
import recipeRealm.observer.KitchenEventPublisher;
import recipeRealm.repository.InventoryRepository;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


public class OrderService {

    private final KitchenEventPublisher eventPublisher;
    private final InventoryRepository inventoryRepository;
    private final Deque<CustomerOrder> orderQueue = new ArrayDeque<>();
    private final List<CustomerOrder> completedOrders = new ArrayList<>();

    public OrderService(KitchenEventPublisher eventPublisher,
                        InventoryRepository inventoryRepository) {
        this.eventPublisher = eventPublisher;
        this.inventoryRepository = inventoryRepository;
    }

    public void enqueueOrder(CustomerOrder order) {
        orderQueue.addLast(order);
        order.setStatus(CustomerOrder.Status.PENDING);
    }

 
    public CookingResult processNextOrder(int playerSkillLevel) {
        CustomerOrder order = orderQueue.pollFirst();
        if (order == null) return null;

        if (order.isExpired()) {
            order.setStatus(CustomerOrder.Status.EXPIRED);
            completedOrders.add(order);
            eventPublisher.notifyDishCompleted(order,
                    new CookingResult(false, 0, 0, "N/A", "Order expired before cooking."));
            return null;
        }

        order.setStatus(CustomerOrder.Status.IN_PROGRESS);
        Recipe recipe = order.getRequestedRecipe();

        inventoryRepository.consumeIngredients(recipe.getIngredients());

        CookingResult result = recipe.cook(playerSkillLevel);

        eventPublisher.notifyDishCompleted(order, result);

        completedOrders.add(order);
        return result;
    }

    public int getQueueSize()                  { return orderQueue.size(); }
    public List<CustomerOrder> getCompletedOrders() { return new ArrayList<>(completedOrders); }
    public boolean hasOrders()                 { return !orderQueue.isEmpty(); }
}
