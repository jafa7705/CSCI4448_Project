package recipeRealm.model;

import recipeRealm.cooking.CookingStrategy;
import java.util.List;


public class Appetizer extends BaseRecipe {

    public Appetizer(String name, String description, List<Ingredient> ingredients,
                     CookingStrategy cookingStrategy, int requiredSkillLevel, int complexity) {
        super(name, description, ingredients, cookingStrategy, requiredSkillLevel,
              complexity, 30 + complexity * 10);
    }

    @Override
    public double getBasePrice() {
        return 5.0 + getComplexity();
    }

    @Override
    public String getCategory() {
        return "Appetizer";
    }
}