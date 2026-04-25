package recipeRealm.cooking;

import recipeRealm.model.Recipe;
import recipeRealm.model.CookingResult;


public interface CookingStrategy {

    CookingResult cook(Recipe recipe, int skillLevel);

    String getMethodName();

    int getBaseTimeSeconds();
}