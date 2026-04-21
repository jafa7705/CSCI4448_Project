package recipeRealm.decorator;

import recipeRealm.model.Recipe;

/**
 * Decorator that creates a double-serving version of a recipe.
 * Doubles both the price and the preparation time.
 */
public class DoubleServingDecorator extends RecipeDecorator {

    public DoubleServingDecorator(Recipe recipe) {
        super(recipe);
    }

    @Override
    public String getName() {
        return wrapped.getName() + " (Double)";
    }

    @Override
    public double getBasePrice() {
        return wrapped.getBasePrice() * 2.0;
    }

    @Override
    public int getPreparationTimeSeconds() {
        return wrapped.getPreparationTimeSeconds() * 2;
    }
}