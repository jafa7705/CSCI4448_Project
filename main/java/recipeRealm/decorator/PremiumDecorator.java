package recipeRealm.decorator;

import recipeRealm.model.Recipe;

/**
 * Decorator that upgrades a recipe to a "Premium" version.
 * Multiplies the price by 1.5× and raises the required skill level by 1.
 */
public class PremiumDecorator extends RecipeDecorator {

    public PremiumDecorator(Recipe recipe) {
        super(recipe);
    }

    @Override
    public String getName() {
        return "Premium " + wrapped.getName();
    }

    @Override
    public double getBasePrice() {
        return wrapped.getBasePrice() * 1.5;
    }

    @Override
    public int getRequiredSkillLevel() {
        return Math.min(10, wrapped.getRequiredSkillLevel() + 1);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " (Made with premium ingredients)";
    }
}