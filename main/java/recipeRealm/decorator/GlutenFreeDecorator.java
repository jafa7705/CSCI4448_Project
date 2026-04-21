package recipeRealm.decorator;

import recipeRealm.model.Recipe;

/**
 * Decorator that produces a gluten-free version of a recipe.
 * Adds a $3.00 surcharge and extends preparation time by 10 seconds
 * (gluten-free substitutes require extra care during prep).
 */
public class GlutenFreeDecorator extends RecipeDecorator {

    public GlutenFreeDecorator(Recipe recipe) {
        super(recipe);
    }

    @Override
    public String getName() {
        return wrapped.getName() + " (GF)";
    }

    @Override
    public double getBasePrice() {
        return wrapped.getBasePrice() + 3.0;
    }

    @Override
    public int getPreparationTimeSeconds() {
        return wrapped.getPreparationTimeSeconds() + 10;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " [Gluten-free substitutes used]";
    }
}