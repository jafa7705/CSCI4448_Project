package recipeRealm.decorator;

import recipeRealm.cooking.CookingStrategy;
import recipeRealm.model.CookingResult;
import recipeRealm.model.Ingredient;
import recipeRealm.model.Recipe;

import java.util.List;


public abstract class RecipeDecorator implements Recipe {

    protected final Recipe wrapped;

    protected RecipeDecorator(Recipe wrapped) {
        this.wrapped = wrapped;
    }

    @Override public String getId(){
        return wrapped.getId(); 
        }
    @Override public String getName(){ 
        return wrapped.getName(); 
    }
    @Override public String getDescription(){ 
        return wrapped.getDescription(); 
    }
    @Override public List<Ingredient> getIngredients(){ 
        return wrapped.getIngredients(); 
    }
    @Override public CookingStrategy getCookingStrategy() { 
        return wrapped.getCookingStrategy(); 
    }
    @Override public int getRequiredSkillLevel(){
        return wrapped.getRequiredSkillLevel(); 
    }
    @Override public int getComplexity(){ 
        return wrapped.getComplexity(); 
    }
    @Override public double getBasePrice(){ 
        return wrapped.getBasePrice(); 
    }
    @Override public String getCategory(){ 
        return wrapped.getCategory(); 
    }
    @Override public int getPreparationTimeSeconds(){ 
        return wrapped.getPreparationTimeSeconds(); 
    }
}