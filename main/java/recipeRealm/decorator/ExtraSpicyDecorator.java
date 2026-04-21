package recipeRealm.decorator;

import recipeRealm.model.Recipe;

/**
 * Decorator that adds an "Extra Spicy" variant to any recipe.
 * Increases price by $2.00 and bumps complexity by 1 (capped at 5).
 */
public class ExtraSpicyDecorator extends RecipeDecorator {

    public ExtraSpicyDecorator(Recipe recipe) {
        super(recipe);
    }

    @Override
    public String getName() {
        return wrapped.getName() + " (Extra Spicy)";
    }

    @Override
    public double getBasePrice() {
        return wrapped.getBasePrice() + 2.0;
    }

    @Override
    public int getComplexity() {
        return Math.min(5, wrapped.getComplexity() + 1);
    }
}