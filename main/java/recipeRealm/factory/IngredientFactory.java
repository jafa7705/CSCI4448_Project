package recipeRealm.factory;

import recipeRealm.model.Ingredient;

import java.time.LocalDate;

/**
 * Factory Pattern: IngredientFactory.
 *
 * Centralises the creation of Ingredient objects so that callers never need
 * to remember default quantities, units, or expiry policies. Each static
 * factory method represents a logical ingredient category with sensible
 * defaults — callers may override quantities where needed.
 */
public class IngredientFactory {

    private IngredientFactory() {}

    /**
     * Creates a generic ingredient with an explicit expiry date.
     */
    public static Ingredient createPerishable(String id, String name, String unit,
                                              double quantity, double requiredAmount,
                                              double costPerUnit, LocalDate expiryDate) {
        Ingredient ingredient = new Ingredient(id, name, unit, quantity, requiredAmount, costPerUnit);
        ingredient.setExpiryDate(expiryDate);
        return ingredient;
    }

    /**
     * Creates a non-perishable ingredient (no expiry date).
     */
    public static Ingredient createNonPerishable(String id, String name, String unit,
                                                  double quantity, double requiredAmount,
                                                  double costPerUnit) {
        return new Ingredient(id, name, unit, quantity, requiredAmount, costPerUnit);
    }

    // ------------------------------------------------------------------ //
    // Convenience factories for common pantry categories                  //
    // ------------------------------------------------------------------ //

    /** Dairy ingredient, expires in the given number of days from today. */
    public static Ingredient createDairy(String id, String name, String unit,
                                         double quantity, double requiredAmount,
                                         double costPerUnit, int daysUntilExpiry) {
        return createPerishable(id, name, unit, quantity, requiredAmount,
                costPerUnit, LocalDate.now().plusDays(daysUntilExpiry));
    }

    /** Dry / pantry staple (flour, sugar, salt, spices) — no expiry. */
    public static Ingredient createDryGood(String id, String name,
                                            double quantityGrams, double requiredGrams,
                                            double costPerGram) {
        return createNonPerishable(id, name, "g", quantityGrams, requiredGrams, costPerGram);
    }

    /** Fresh produce that expires in the given number of days from today. */
    public static Ingredient createProduce(String id, String name,
                                            double quantity, double requiredAmount,
                                            double costPerUnit, int freshDays) {
        return createPerishable(id, name, "pieces", quantity, requiredAmount,
                costPerUnit, LocalDate.now().plusDays(freshDays));
    }

    /** Protein (meat, fish, eggs) that expires in the given number of days. */
    public static Ingredient createProtein(String id, String name, String unit,
                                            double quantity, double requiredAmount,
                                            double costPerUnit, int freshDays) {
        return createPerishable(id, name, unit, quantity, requiredAmount,
                costPerUnit, LocalDate.now().plusDays(freshDays));
    }
}