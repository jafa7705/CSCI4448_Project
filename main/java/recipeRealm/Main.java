package recipeRealm;

import recipeRealm.model.CookingResult;

/**
 * Entry point for Recipe Realm.
 *
 * Boots the GameManager with a starting skill level, seeds the default
 * recipe catalogue and inventory, then runs a short demonstration loop
 * that simulates several customer orders being placed and fulfilled.
 *
 * In the full game this would be replaced by a JavaFX Application that
 * connects UI events to GameManager calls.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Welcome to Recipe Realm!");
        System.out.println("==========================================\n");

        GameManager game = new GameManager(3); // start at skill level 3
        game.seedDefaultData();

        System.out.println("Recipes loaded: " + game.getAllRecipes().size());
        System.out.println("Starting skill level: " + game.getPlayerSkillLevel());
        System.out.println();

        // Simulate a short game session — submit and process 5 orders
        for (int round = 1; round <= 5; round++) {
            System.out.println("--- Round " + round + " ---");
            game.submitRandomOrder();
            CookingResult result = game.processNextOrder();
            if (result != null) {
                System.out.println("  " + result);
            }
            System.out.println();
        }

        // Level up and play two more rounds
        game.levelUp();
        System.out.println("--- Bonus rounds at skill level " + game.getPlayerSkillLevel() + " ---");
        for (int round = 1; round <= 2; round++) {
            game.submitRandomOrder();
            CookingResult result = game.processNextOrder();
            if (result != null) {
                System.out.println("  " + result);
            }
            System.out.println();
        }

        // Final summary
        System.out.println("==========================================");
        System.out.printf("  Total earnings:        $%.2f%n", game.getTotalEarnings());
        System.out.printf("  Average satisfaction:  %d%%%n",  game.getAverageSatisfaction());
        System.out.println("==========================================");
    }
}