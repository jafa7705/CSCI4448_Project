package recipeRealm.factory;

import recipeRealm.cooking.BakingStrategy;
import recipeRealm.cooking.CookingStrategy;
import recipeRealm.cooking.FryingStrategy;
import recipeRealm.cooking.GrillingStrategy;
import recipeRealm.cooking.SteamingStrategy;
import recipeRealm.model.Appetizer;
import recipeRealm.model.Dessert;
import recipeRealm.model.Ingredient;
import recipeRealm.model.MainCourse;
import recipeRealm.model.Recipe;

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

    // ------------------------------------------------------------------ //
    // Private helpers                                                      //
    // ------------------------------------------------------------------ //

    private static CookingStrategy resolveStrategy(String cookingMethod) {
        return switch (cookingMethod.toLowerCase()) {
            case "baking"    -> new BakingStrategy();
            case "frying"    -> new FryingStrategy();
            case "grilling"  -> new GrillingStrategy();
            case "steaming"  -> new SteamingStrategy();
            default -> throw new IllegalArgumentException(
                    "Unknown cooking method: " + cookingMethod
                    + ". Valid methods: baking, frying, grilling, steaming.");
        };
    }
}