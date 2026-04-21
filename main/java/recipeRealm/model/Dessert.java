package recipeRealm.model;

import recipeRealm.cooking.CookingStrategy;
import java.util.List;

/**
 * Concrete recipe subtype representing a dessert.
 * Desserts require moderate preparation time (45 + complexity × 20 seconds)
 * and are priced at $7.00 + complexity × $1.80.
 */
public class Dessert extends BaseRecipe {

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
    public String getCategory() {
        return "Dessert";
    }
}