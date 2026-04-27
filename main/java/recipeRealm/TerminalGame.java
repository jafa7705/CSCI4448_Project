package recipeRealm;

import recipeRealm.decorator.DoubleServingDecorator;
import recipeRealm.decorator.ExtraSpicyDecorator;
import recipeRealm.decorator.GlutenFreeDecorator;
import recipeRealm.decorator.PremiumDecorator;
import recipeRealm.factory.CustomerOrderFactory;
import recipeRealm.model.CookingResult;
import recipeRealm.model.CustomerOrder;
import recipeRealm.model.Ingredient;
import recipeRealm.model.Recipe;
import recipeRealm.service.RecipeService;

import java.util.List;
import java.util.Scanner;

 // Run cmd java -cp out recipeRealm.TerminalGame
public class TerminalGame {

    private static final String DIVIDER  = "─────────────────────────────────────────";
    private static final String DIVIDER2 = "═════════════════════════════════════════";

    private final GameManager game;
    private final Scanner scanner;

    private static final int[] XP_THRESHOLDS = { 0, 3, 7, 12, 18, 25, 33, 42, 52, 63 };
    private int ordersCompleted = 0;

    public TerminalGame() {
        this.game    = new GameManager(1);
        this.scanner = new Scanner(System.in);
        game.seedDefaultData();
    }

    public static void main(String[] args) {
        new TerminalGame().run();
    }

    public void run() {
        printSplash();
        boolean playing = true;
        while (playing) {
            printStatus();
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleTakeOrder();
                case "2" -> handleViewRecipes();
                case "3" -> handleViewInventory();
                case "4" -> handleRecipeBook();
                case "q" -> playing = false;
                default  -> System.out.println("  Unknown option — try again.\n");
            }
        }
        printFinalSummary();
        scanner.close();
    }


    private void printSplash() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║       R E C I P E   R E A L M        ║");
        System.out.println("  ║      Interactive Cooking Game         ║");
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.println();
        System.out.println("  You're a chef running your own restaurant.");
        System.out.println("  Cook orders, earn money, and level up your skills!");
        System.out.println("  Complete orders to gain XP and unlock harder recipes.");
        System.out.println();
        pressEnter();
    }

    private void printStatus() {
        System.out.println(DIVIDER2);
        System.out.printf("  Skill: %s  |  Earnings: $%.2f  |  Average Satisfaction: %d%%%n",
                skillBar(game.getPlayerSkillLevel()),
                game.getTotalEarnings(),
                game.getAverageSatisfaction());
        int nextThreshold = nextXpThreshold();
        if (nextThreshold > 0) {
            System.out.printf("  XP: %d / %d orders to level up%n",
                    ordersCompleted, nextThreshold);
        } else {
            System.out.println("  XP: MAX LEVEL reached!");
        }
        System.out.println(DIVIDER2);
        System.out.println();
    }

    private void printMainMenu() {
        System.out.println("  What would you like to do?");
        System.out.println();
        System.out.println("  1  Take a customer order");
        System.out.println("  2  View all recipes");
        System.out.println("  3  View inventory");
        System.out.println("  4  Recipe book (browse & decorate)");
        System.out.println("  q  Quit");
        System.out.println();
        System.out.print("  > ");
    }

    
    private void handleTakeOrder() {
        System.out.println();
        System.out.println(DIVIDER);

        RecipeService rs = game.getRecipeService();
        List<Recipe> available = rs.getAvailableRecipes(game.getPlayerSkillLevel());

        if (available.isEmpty()) {
            System.out.println("  No recipes available at your skill level yet.");
            System.out.println();
            return;
        }

        CustomerOrder order = CustomerOrderFactory.createRandom(available);
        Recipe recipe = order.getRequestedRecipe();

        System.out.println("  A customer arrives!");
        System.out.println();
        System.out.printf("  Order:    %s (%s)%n", recipe.getName(), recipe.getCategory());
        System.out.printf("  Method:   %s%n", recipe.getCookingStrategy().getMethodName());
        System.out.printf("  Skill req: %d  |  Complexity: %d%n",
                recipe.getRequiredSkillLevel(), recipe.getComplexity());
        System.out.printf("  Price:    $%.2f%n", recipe.getBasePrice());
        System.out.printf("  Patience: %d seconds%n", order.getPatienceSeconds());
        System.out.println();

        Recipe finalRecipe = offerDecoration(recipe);
        if (finalRecipe != recipe) {
            order = CustomerOrderFactory.createForRecipe(finalRecipe);
        }

        System.out.println();
        System.out.println("  [c] Cook it   [s] Skip this order");
        System.out.print("  > ");
        String input = scanner.nextLine().trim().toLowerCase();

        if (!input.equals("c")) {
            System.out.println("  Order skipped.\n");
            return;
        }

        game.getOrderService().enqueueOrder(order);
        System.out.println();
        System.out.println("  Cooking...");
        CookingResult result = game.processNextOrder();

        if (result == null) {
            System.out.println("  The order expired before you could cook it!\n");
            return;
        }

        printCookingResult(result, order);

        List<Ingredient> alerts = game.getStockObserver().getAlerts();

        for (Ingredient ingredient: alerts){
            System.out.println(" WARNING: " + ingredient.getName() + " is Low/Expired");
            System.out.println("  1  Buy more    2  Remove affected recipes");
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")){
                double cost = ingredient.getRequiredAmount() * ingredient.getCostPerUnit();
                if(cost > game.getTotalEarnings()){
                    game.getRecipeService().removeRecipeWithIngredient(ingredient);
                    System.out.println("Insufficient funds Removed all recipes using " + ingredient.getName());
                }else{
                    game.spendFunds(cost);
                    game.getInventoryService().restockIngredient(ingredient.getId(), ingredient.getRequiredAmount());
                }

            }else{
                game.getRecipeService().removeRecipeWithIngredient(ingredient);
                System.out.println("  Removed all recipes using " + ingredient.getName());
            }
        }
        game.getStockObserver().clearAlerts();
        ordersCompleted++;
        checkLevelUp();
        System.out.println();
    }



    private Recipe offerDecoration(Recipe base) {
        System.out.println("  Add a special variation? (optional)");
        System.out.println("  1  Extra Spicy  (+$2.00, +1 complexity)");
        System.out.println("  2  Premium      (×1.5 price, +1 skill req)");
        System.out.println("  3  Double       (×2 price, ×2 prep time)");
        System.out.println("  4  Gluten-Free  (+$3.00, +10s prep)");
        System.out.println("  5  No variation");
        System.out.print("  > ");
        String choice = scanner.nextLine().trim();
        return switch (choice) {
            case "1" -> new ExtraSpicyDecorator(base);
            case "2" -> new PremiumDecorator(base);
            case "3" -> new DoubleServingDecorator(base);
            case "4" -> new GlutenFreeDecorator(base);
            default  -> base;
        };
    }

    private void printCookingResult(CookingResult result, CustomerOrder order){
        System.out.println();
        System.out.println(DIVIDER);
        System.out.printf("  Method:   %s%n", result.getCookingMethod());
        System.out.printf("  Score:    %d / 100%n", result.getScore());
        System.out.printf("  Stars:    %s%n", stars(result.getStarRating()));
        System.out.printf("  Time:     %ds%n", result.getTimeTakenSeconds());
        System.out.printf("  Result:   %s%n", result.isSuccess() ? "SUCCESS" : "FAILED");
        System.out.printf("  Satisfaction:   %d / 100%n", game.getLastOrderSatisfaction());
        System.out.printf("  Feedback: %s%n", result.getFeedbackMessage());

        if (result.isSuccess()) {
            double earned = game.getLastOrderEarnings();
            System.out.printf("  Earned:   $%.2f%n", earned);
        }
        
        System.out.println(DIVIDER);
    }

    private void handleViewRecipes() {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("  ALL RECIPES");
        System.out.println(DIVIDER);
        List<Recipe> all = game.getAllRecipes();
        for (Recipe r : all) {
            String lock = r.getRequiredSkillLevel() > game.getPlayerSkillLevel() ? " [LOCKED]" : "";
            System.out.printf("  %-22s  %-12s  Skill: %d  Price: $%.2f%s%n",
                    r.getName(), r.getCategory(),
                    r.getRequiredSkillLevel(), r.getBasePrice(), lock);
        }
        System.out.println(DIVIDER);
        System.out.println();
        pressEnter();
    }

    private void handleViewInventory() {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("  INVENTORY");
        System.out.println(DIVIDER);
        game.getInventoryService().getAllIngredients().forEach(i -> {
            String status = i.isExpired() ? " [EXPIRED]": !i.isAvailable() ? " [LOW STOCK]" : "";
            System.out.printf("  %-14s  %.1f %s%s%n",
                    i.getName(), i.getQuantity(), i.getUnit(), status);
        });
        System.out.println(DIVIDER);
        System.out.println();
        pressEnter();
    }

    private void handleRecipeBook() {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("  RECIPE BOOK — pick a recipe to inspect");
        System.out.println(DIVIDER);

        List<Recipe> all = game.getAllRecipes();
        for (int i = 0; i < all.size(); i++) {
            Recipe r = all.get(i);
            System.out.printf("  %d  %s%n", i + 1, r.getName());
        }
        System.out.println("  b  Back");
        System.out.print("  > ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("b")) return;

        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx < 0 || idx >= all.size()) {
                System.out.println("  Invalid selection.\n");
                return;
            }
            printRecipeDetail(all.get(idx));
        } catch (NumberFormatException e) {
            System.out.println("  Invalid selection.\n");
        }
    }

    private void printRecipeDetail(Recipe r) {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.printf("  %s  (%s)%n", r.getName(), r.getCategory());
        System.out.println(DIVIDER);
        System.out.printf("  %s%n", r.getDescription());
        System.out.println();
        System.out.printf("  Cooking method:  %s%n", r.getCookingStrategy().getMethodName());
        System.out.printf("  Skill required:  %d%n", r.getRequiredSkillLevel());
        System.out.printf("  Complexity:      %d / 5%n", r.getComplexity());
        System.out.printf("  Prep time:       %ds%n", r.getPreparationTimeSeconds());
        System.out.printf("  Base price:      $%.2f%n", r.getBasePrice());
        System.out.println();
        System.out.println("  Ingredients:");
        r.getIngredients().forEach(i ->
            System.out.printf("    - %s  %.1f %s%n", i.getName(), i.getRequiredAmount(), i.getUnit()));
        System.out.println(DIVIDER);
        System.out.println();
        pressEnter();
    }

    private boolean confirmQuit() {
        System.out.print("  Quit the game? (y/n) > ");
        String answer = scanner.hasNextLine() ? scanner.nextLine().trim() : "y";
        if (answer.equalsIgnoreCase("y")) return true;
        System.out.println("  Returning to menu.\n");
        return false;
    }

   
    private void checkLevelUp() {
        int current = game.getPlayerSkillLevel();
        if (current >= 10) return;
        if (ordersCompleted >= XP_THRESHOLDS[current]) {
            game.levelUp();
            System.out.println();
            System.out.println("  ★ LEVEL UP! ★");
            System.out.printf("  You are now a skill-%d chef!%n", game.getPlayerSkillLevel());
            List<Recipe> newUnlocks = game.getRecipeService()
                    .getAvailableRecipes(game.getPlayerSkillLevel());
            long newCount = newUnlocks.stream()
                    .filter(r -> r.getRequiredSkillLevel() == game.getPlayerSkillLevel())
                    .count();
            if (newCount > 0) {
                System.out.printf("  %d new recipe(s) unlocked — check the recipe book!%n", newCount);
            }
        }
    }

    private int nextXpThreshold() {
        int current = game.getPlayerSkillLevel();
        if (current >= 10) return -1;
        return XP_THRESHOLDS[current];
    }

   
    private void printFinalSummary() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║           SESSION SUMMARY            ║");
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.printf("  Orders completed:    %d%n", ordersCompleted);
        System.out.printf("  Total earnings:      $%.2f%n", game.getTotalEarnings());
        System.out.printf("  Avg satisfaction:    %d%%%n", game.getAverageSatisfaction());
        System.out.printf("  Final skill level:   %d / 10%n", game.getPlayerSkillLevel());
        System.out.println();
        System.out.println("  Thanks for playing!");
        System.out.println();
    }

 

    private String skillBar(int level) {
        StringBuilder sb = new StringBuilder("Lv." + level + " [");
        for (int i = 1; i <= 10; i++) sb.append(i <= level ? "█" : "░");
        sb.append("]");
        return sb.toString();
    }

    private String stars(int count) {
        return "★".repeat(count) + "☆".repeat(5 - count);
    }

    private void pressEnter() {
        System.out.print("  Press Enter to continue...");
        scanner.nextLine();
        System.out.println();
    }
}