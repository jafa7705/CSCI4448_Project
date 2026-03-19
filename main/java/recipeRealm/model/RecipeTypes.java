package recipeRealm.model;

import recipeRealm.cooking.CookingStrategy;
import java.util.List;


class Appetizer extends BaseRecipe {

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
    public String getCategory() { return "Appetizer"; }
}

class MainCourse extends BaseRecipe {

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
    public String getCategory() { return "MainCourse"; }
}

class Dessert extends BaseRecipe {

    public Dessert(String name, String description, List<Ingredient> ingredients,
                   CookingStrategy cookingStrategy, int requiredSkillLevel, int complexity) {
        super(name, description, ingredients, cookingStrategy, requiredSkillLevel,
              complexity, 45 + complexity * 20);
    }

    @Override
    public double getBasePrice() {
        return 7.0 + (getComplexity() * 1.8);
    }

    @Override
    public String getCategory() { return "Dessert"; }
}
