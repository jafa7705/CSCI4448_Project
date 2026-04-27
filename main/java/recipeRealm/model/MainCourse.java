package recipeRealm.model;

import recipeRealm.cooking.CookingStrategy;
import java.util.List;


public class MainCourse extends BaseRecipe {

    public MainCourse(String name, String description, List<Ingredient> ingredients,
                      CookingStrategy cookingStrategy, int requiredSkillLevel, int complexity) {
        super(name, description, ingredients, cookingStrategy, requiredSkillLevel,
              complexity, 60 + complexity * 15);
    }

    @Override
    public double getBasePrice() {
        return 12.0 + (getComplexity() * 2.5);
    }

    @Override
    public String getCategory() {
        return "MainCourse";
    }
}