package recipeRealm.factory;

import recipeRealm.cooking.*;
import recipeRealm.model.*;
import java.util.List;

public class RecipeFactory {

    private RecipeFactory() {}

    public static Recipe createAppetizer(String name, String description,
                                         List<Ingredient> ingredients,
                                         String cookingMethod,
                                         int skillLevel, int complexity) {
        CookingStrategy strategy = resolveStrategy(cookingMethod);
        return new Appetizer(name, description, ingredients, strategy, skillLevel, complexity);
    }

    public static Recipe createMainCourse(String name, String description,
                                          List<Ingredient> ingredients,
                                          String cookingMethod,
                                          int skillLevel, int complexity) {
        CookingStrategy strategy = resolveStrategy(cookingMethod);
        return new MainCourse(name, description, ingredients, strategy, skillLevel, complexity);
    }

    public static Recipe createDessert(String name, String description,
                                       List<Ingredient> ingredients,
                                       String cookingMethod,
                                       int skillLevel, int complexity) {
        CookingStrategy strategy = resolveStrategy(cookingMethod);
        return new Dessert(name, description, ingredients, strategy, skillLevel, complexity);
    }


    private static CookingStrategy resolveStrategy(String cookingMethod) {
        return switch (cookingMethod.toLowerCase()) {
            case "baking"   -> new BakingStrategy();
            case "frying"   -> new FryingStrategy();
            case "grilling" -> new GrillingStrategy();
            default -> throw new IllegalArgumentException(
                    "Unknown cooking method: " + cookingMethod);
        };
    }
}
