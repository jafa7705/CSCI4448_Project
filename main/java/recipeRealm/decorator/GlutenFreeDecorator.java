package recipeRealm.decorator;

import recipeRealm.model.Recipe;


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