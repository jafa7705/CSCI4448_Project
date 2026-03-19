package recipeRealm.decorator;

import recipeRealm.cooking.CookingStrategy;
import recipeRealm.model.*;
import java.util.List;

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


class ExtraSpicyDecorator extends RecipeDecorator {

    public ExtraSpicyDecorator(Recipe recipe) { super(recipe); }

    @Override public String getName()     { return wrapped.getName() + " (Extra Spicy)"; }
    @Override public double getBasePrice(){ return wrapped.getBasePrice() + 2.0; }
    @Override public int getComplexity()  { return Math.min(5, wrapped.getComplexity() + 1); }
}

class PremiumDecorator extends RecipeDecorator {

    public PremiumDecorator(Recipe recipe) { super(recipe); }

    @Override public String getName()          { return "Premium " + wrapped.getName(); }
    @Override public double getBasePrice()     { return wrapped.getBasePrice() * 1.5; }
    @Override public int getRequiredSkillLevel(){ return Math.min(10, wrapped.getRequiredSkillLevel() + 1); }
    @Override public String getDescription()   {
        return wrapped.getDescription() + " (Made with premium ingredients)";
    }
}

class DoubleServingDecorator extends RecipeDecorator {

    public DoubleServingDecorator(Recipe recipe) { super(recipe); }

    @Override public String getName()               { return wrapped.getName() + " (Double)"; }
    @Override public double getBasePrice()          { return wrapped.getBasePrice() * 2.0; }
    @Override public int getPreparationTimeSeconds(){ return wrapped.getPreparationTimeSeconds() * 2; }
}
