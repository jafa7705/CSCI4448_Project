package recipeRealm.decorator;

import recipeRealm.cooking.CookingStrategy;
import recipeRealm.model.CookingResult;
import recipeRealm.model.Ingredient;
import recipeRealm.model.Recipe;

import java.util.List;

/**
 * Decorator Pattern: Abstract base decorator for Recipe.
 *
 * Wraps a Recipe and delegates all method calls to the wrapped instance.
 * Concrete subclasses override only the methods they wish to modify
 * (e.g., getName, getBasePrice, getComplexity) without touching the rest.
 *
 * This avoids combinatorial explosion of subclasses — instead of creating
 * PremiumSpicyDoubleServing as a dedicated class, decorators stack freely.
 */
public abstract class RecipeDecorator implements Recipe {

    protected final Recipe wrapped;

    protected RecipeDecorator(Recipe wrapped) {
        this.wrapped = wrapped;
    }

    @Override public String getId()                       { return wrapped.getId(); }
    @Override public String getName()                     { return wrapped.getName(); }
    @Override public String getDescription()              { return wrapped.getDescription(); }
    @Override public List<Ingredient> getIngredients()    { return wrapped.getIngredients(); }
    @Override public CookingStrategy getCookingStrategy() { return wrapped.getCookingStrategy(); }
    @Override public int getRequiredSkillLevel()          { return wrapped.getRequiredSkillLevel(); }
    @Override public int getComplexity()                  { return wrapped.getComplexity(); }
    @Override public double getBasePrice()                { return wrapped.getBasePrice(); }
    @Override public String getCategory()                 { return wrapped.getCategory(); }
    @Override public int getPreparationTimeSeconds()      { return wrapped.getPreparationTimeSeconds(); }
}