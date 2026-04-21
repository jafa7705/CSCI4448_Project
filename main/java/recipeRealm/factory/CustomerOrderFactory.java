package recipeRealm.factory;

import recipeRealm.model.CustomerOrder;
import recipeRealm.model.Recipe;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class CustomerOrderFactory {

    private static final Random RANDOM = new Random();

    private static final double MIN_PATIENCE_MULTIPLIER = 1.2;
    private static final double MAX_PATIENCE_MULTIPLIER = 2.5;

    private CustomerOrderFactory() {}

    public static CustomerOrder createRandom(List<Recipe> availableRecipes) {
        if (availableRecipes == null || availableRecipes.isEmpty())
            throw new IllegalArgumentException("Recipe pool must not be empty.");
        Recipe chosen = availableRecipes.get(RANDOM.nextInt(availableRecipes.size()));
        return createForRecipe(chosen);
    }

    public static CustomerOrder createForRecipe(Recipe recipe) {
        double multiplier = MIN_PATIENCE_MULTIPLIER
                + RANDOM.nextDouble() * (MAX_PATIENCE_MULTIPLIER - MIN_PATIENCE_MULTIPLIER);
        int patienceSeconds = (int) (recipe.getPreparationTimeSeconds() * multiplier);
        String customerId = "customer-" + UUID.randomUUID().toString().substring(0, 8);
        return new CustomerOrder(customerId, recipe, patienceSeconds);
    }

    public static CustomerOrder createImpatient(Recipe recipe) {
        int patienceSeconds = (int) (recipe.getPreparationTimeSeconds() * MIN_PATIENCE_MULTIPLIER);
        String customerId = "impatient-" + UUID.randomUUID().toString().substring(0, 8);
        return new CustomerOrder(customerId, recipe, patienceSeconds);
    }
}