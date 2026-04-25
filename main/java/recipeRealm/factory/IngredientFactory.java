package recipeRealm.factory;

import recipeRealm.model.Ingredient;

import java.time.LocalDate;


public class IngredientFactory {

    private IngredientFactory() {}

    public static Ingredient createPerishable(String id, String name, String unit,
                                              double quantity, double requiredAmount,
                                              double costPerUnit, LocalDate expiryDate) {
        Ingredient ingredient = new Ingredient(id, name, unit, quantity, requiredAmount, costPerUnit);
        ingredient.setExpiryDate(expiryDate);
        return ingredient;
    }


    public static Ingredient createNonPerishable(String id, String name, String unit,
                                                  double quantity, double requiredAmount,
                                                  double costPerUnit) {
        return new Ingredient(id, name, unit, quantity, requiredAmount, costPerUnit);
    }


    public static Ingredient createDairy(String id, String name, String unit,
                                         double quantity, double requiredAmount,
                                         double costPerUnit, int daysUntilExpiry) {
        return createPerishable(id, name, unit, quantity, requiredAmount,
                costPerUnit, LocalDate.now().plusDays(daysUntilExpiry));
    }


    public static Ingredient createDryGood(String id, String name,
                                            double quantityGrams, double requiredGrams,
                                            double costPerGram) {
        return createNonPerishable(id, name, "g", quantityGrams, requiredGrams, costPerGram);
    }


    public static Ingredient createProduce(String id, String name,
                                            double quantity, double requiredAmount,
                                            double costPerUnit, int freshDays) {
        return createPerishable(id, name, "pieces", quantity, requiredAmount,
                costPerUnit, LocalDate.now().plusDays(freshDays));
    }


    public static Ingredient createProtein(String id, String name, String unit,
                                            double quantity, double requiredAmount,
                                            double costPerUnit, int freshDays) {
        return createPerishable(id, name, unit, quantity, requiredAmount,
                costPerUnit, LocalDate.now().plusDays(freshDays));
    }
}