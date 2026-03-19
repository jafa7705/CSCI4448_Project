package recipeRealm.cooking;

import recipeRealm.model.Recipe;
import recipeRealm.model.CookingResult;

/**
 * Strategy Pattern: CookingStrategy interface.
 *
 * Each cooking method (Baking, Frying, Grilling) implements this interface
 * with its own unique logic for time calculation, difficulty modifiers, and
 * success scoring. Recipes hold a reference to a CookingStrategy and delegate
 * all cooking behaviour to it — no if/else chains needed in Recipe or GameManager.
 */
public interface CookingStrategy {

    /**
     * Execute the cooking process for the given recipe.
     *
     * @param recipe   the recipe being cooked
     * @param skillLevel player's current skill level (1–10)
     * @return a CookingResult describing success, score, and time taken
     */
    CookingResult cook(Recipe recipe, int skillLevel);

    /**
     * Returns the display name of this cooking method (e.g. "Baking").
     */
    String getMethodName();

    /**
     * Returns the base time in seconds this method requires, before recipe
     * modifiers are applied.
     */
    int getBaseTimeSeconds();
}
