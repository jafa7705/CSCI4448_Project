package recipeRealm.model;

import recipeRealm.cooking.CookingStrategy;
import java.util.List;

public interface Recipe {

    String getId();
    String getName();
    String getDescription();
    List<Ingredient> getIngredients();
    CookingStrategy getCookingStrategy();
    int getRequiredSkillLevel();    
    int getComplexity();       
    double getBasePrice();
    String getCategory();        
    int getPreparationTimeSeconds();

    default CookingResult cook(int playerSkillLevel) {
        return getCookingStrategy().cook(this, playerSkillLevel);
    }
}