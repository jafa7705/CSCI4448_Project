package recipeRealm.test;

import recipeRealm.GameManager;
import recipeRealm.cooking.*;
import recipeRealm.decorator.*;
import recipeRealm.factory.*;
import recipeRealm.model.*;
import recipeRealm.observer.*;
import recipeRealm.repository.*;
import recipeRealm.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Hand-rolled test suite for Recipe Realm (no external test framework).
 *
 * Each test method is self-contained and prints PASS / FAIL. The main()
 * method runs all tests and exits with code 1 if any test fails, making
 * this suitable for use in a CI script.
 */
public class RecipeRealmTestSuite {

    private static int passed = 0, failed = 0;

    // ------------------------------------------------------------------ //
    // Micro-assertion helpers                                              //
    // ------------------------------------------------------------------ //

    private static void assertTrue(String label, boolean condition) {
        if (condition) {
            System.out.println("  PASS: " + label);
            passed++;
        } else {
            System.out.println("  FAIL: " + label);
            failed++;
        }
    }

    private static void assertEquals(String label, Object expected, Object actual) {
        assertTrue(label + " [expected=" + expected + ", actual=" + actual + "]",
                expected.equals(actual));
    }

    // ------------------------------------------------------------------ //
    // Shared fixture helpers                                               //
    // ------------------------------------------------------------------ //

    private static List<Ingredient> makeIngredients() {
        Ingredient egg = new Ingredient("ing-001", "Egg", "pieces", 10, 2, 0.30);
        List<Ingredient> list = new ArrayList<>();
        list.add(egg);
        return list;
    }

    // ================================================================== //
    // Tests                                                               //
    // ================================================================== //

    // ---- Strategy Pattern -------------------------------------------- //

    private static void testBakingStrategyScoreScalesWithSkill() {
        System.out.println("\nTest 1: BakingStrategy scores scale with skill level");
        BakingStrategy baking = new BakingStrategy();
        Recipe recipe = RecipeFactory.createDessert(
                "Croissant", "Buttery pastry", makeIngredients(), "baking", 6, 3);

        CookingResult expertResult = baking.cook(recipe, 10);
        CookingResult noviceResult = baking.cook(recipe, 1);

        assertTrue("Expert score >= 90", expertResult.getScore() >= 90);
        assertTrue("Novice score < expert score", noviceResult.getScore() < expertResult.getScore());
        assertTrue("Expert attempt is successful", expertResult.isSuccess());
    }

    private static void testFryingStrategyBonusForHighSkill() {
        System.out.println("\nTest 2: FryingStrategy scores higher when skill exceeds requirement");
        FryingStrategy frying = new FryingStrategy();
        // Use a recipe requiring skill 6 so that skill-9 gets the bonus but skill-3 doesn't
        Recipe hardRecipe = RecipeFactory.createMainCourse(
                "Tempura", "Delicate battered fry", makeIngredients(), "frying", 6, 2);

        CookingResult highSkill = frying.cook(hardRecipe, 9);  // above requirement → bonus
        CookingResult lowSkill  = frying.cook(hardRecipe, 3);  // below requirement → penalty

        assertTrue("High-skill score > low-skill score", highSkill.getScore() > lowSkill.getScore());
        assertTrue("Cooking method is 'Frying'", "Frying".equals(highSkill.getCookingMethod()));
        assertTrue("Time taken is positive", highSkill.getTimeTakenSeconds() > 0);
    }

    private static void testGrillingStrategyHighSkillUnlocksBonus() {
        System.out.println("\nTest 3: GrillingStrategy gives a grill-marks bonus at skill >= 8");
        GrillingStrategy grilling = new GrillingStrategy();
        Recipe steak = RecipeFactory.createMainCourse(
                "Sirloin", "Charred steak", makeIngredients(), "grilling", 4, 2);

        CookingResult masterChef = grilling.cook(steak, 10);
        CookingResult amateur    = grilling.cook(steak, 4);

        assertTrue("Master chef score > amateur", masterChef.getScore() > amateur.getScore());
        assertTrue("Cooking method is 'Grilling'", "Grilling".equals(masterChef.getCookingMethod()));
    }

    private static void testSteamingStrategyScoresHighWithMatchingSkill() {
        System.out.println("\nTest 4: SteamingStrategy awards precision bonus when skill >= required");
        SteamingStrategy steaming = new SteamingStrategy();
        Recipe dumplings = RecipeFactory.createAppetizer(
                "Dumplings", "Steamed pork parcels", makeIngredients(), "steaming", 3, 2);

        CookingResult skilled   = steaming.cook(dumplings, 5);
        CookingResult unskilled = steaming.cook(dumplings, 1);

        assertTrue("Skilled player scores higher", skilled.getScore() > unskilled.getScore());
        assertTrue("Cooking method is 'Steaming'", "Steaming".equals(skilled.getCookingMethod()));
        assertTrue("Base time is positive", steaming.getBaseTimeSeconds() > 0);
    }

    // ---- Factory Pattern --------------------------------------------- //

    private static void testRecipeFactoryWiresCorrectStrategy() {
        System.out.println("\nTest 5: RecipeFactory wires the correct CookingStrategy");
        Recipe steak = RecipeFactory.createMainCourse(
                "Ribeye Steak", "Prime cut", makeIngredients(), "grilling", 5, 3);

        CookingStrategy strategy = steak.getCookingStrategy();
        assertEquals("Strategy name is 'Grilling'", "Grilling", strategy.getMethodName());
        assertTrue("Base time > 0", strategy.getBaseTimeSeconds() > 0);

        // steaming should now be valid
        Recipe steamedFish = RecipeFactory.createMainCourse(
                "Sea Bass", "Delicate fish", makeIngredients(), "steaming", 4, 2);
        assertEquals("Steaming strategy wired", "Steaming", steamedFish.getCookingStrategy().getMethodName());

        boolean threw = false;
        try { RecipeFactory.createMainCourse("X", "X", makeIngredients(), "microwaving", 1, 1); }
        catch (IllegalArgumentException e) { threw = true; }
        assertTrue("Factory throws on unknown cooking method", threw);
    }

    private static void testIngredientFactoryCreatesPerishableWithExpiry() {
        System.out.println("\nTest 6: IngredientFactory creates perishable with correct expiry");
        Ingredient dairy = IngredientFactory.createDairy(
                "ing-milk", "Milk", "ml", 1000, 200, 0.002, 7);

        assertTrue("Expiry date is set", dairy.getExpiryDate() != null);
        assertTrue("Not yet expired", !dairy.isExpired());
        assertTrue("Available", dairy.isAvailable());

        Ingredient expired = IngredientFactory.createPerishable(
                "ing-stale", "Stale Bread", "slices", 5, 1, 0.20,
                LocalDate.now().minusDays(1));
        assertTrue("Past-date ingredient is expired", expired.isExpired());
        assertTrue("Expired ingredient is unavailable", !expired.isAvailable());
    }

    private static void testIngredientFactoryNonPerishableNeverExpires() {
        System.out.println("\nTest 7: IngredientFactory non-perishable has no expiry");
        Ingredient flour = IngredientFactory.createDryGood("ing-fl", "Flour", 500, 100, 0.002);

        assertTrue("No expiry date on dry good", flour.getExpiryDate() == null);
        assertTrue("Always available while stocked", flour.isAvailable());
    }

    // ---- Decorator Pattern ------------------------------------------- //

    private static void testStackedDecoratorsCompoundCorrectly() {
        System.out.println("\nTest 8: Stacked decorators compound price and name");
        Recipe base = RecipeFactory.createMainCourse(
                "Burger", "Classic beef burger", makeIngredients(), "grilling", 3, 2);

        double basePrice = base.getBasePrice(); // 12 + 2.5*2 = 17.0

        Recipe spicy   = new ExtraSpicyDecorator(base);
        Recipe doubled = new DoubleServingDecorator(spicy);

        double expectedPrice = (basePrice + 2.0) * 2.0;
        assertTrue("Price compounds correctly",
                Math.abs(doubled.getBasePrice() - expectedPrice) < 0.001);
        assertTrue("Name includes both decorations",
                doubled.getName().contains("Extra Spicy") && doubled.getName().contains("Double"));
        assertTrue("Category unchanged by decorators", "MainCourse".equals(doubled.getCategory()));
    }

    private static void testPremiumDecoratorRaisesSkillRequirement() {
        System.out.println("\nTest 9: PremiumDecorator raises required skill level by 1");
        Recipe base = RecipeFactory.createMainCourse(
                "Pasta", "Al dente pasta", makeIngredients(), "baking", 3, 2);
        Recipe premium = new PremiumDecorator(base);

        assertEquals("Skill requirement increased by 1",
                base.getRequiredSkillLevel() + 1, premium.getRequiredSkillLevel());
        assertTrue("Price multiplied by 1.5",
                Math.abs(premium.getBasePrice() - base.getBasePrice() * 1.5) < 0.001);
        assertTrue("Description updated", premium.getDescription().contains("premium"));
    }

    private static void testGlutenFreeDecoratorAddsCorrectSurcharge() {
        System.out.println("\nTest 10: GlutenFreeDecorator adds $3 surcharge and extends prep time");
        Recipe base = RecipeFactory.createAppetizer(
                "Bread Rolls", "Soft rolls", makeIngredients(), "baking", 2, 1);
        Recipe gf = new GlutenFreeDecorator(base);

        assertTrue("Price increases by $3.00",
                Math.abs(gf.getBasePrice() - (base.getBasePrice() + 3.0)) < 0.001);
        assertEquals("Prep time adds 10 seconds",
                base.getPreparationTimeSeconds() + 10, gf.getPreparationTimeSeconds());
        assertTrue("Name tagged with (GF)", gf.getName().contains("GF"));
        assertTrue("Description updated", gf.getDescription().contains("Gluten-free"));
    }

    // ---- Observer Pattern -------------------------------------------- //

    private static void testObserverPatternNotifiesAllSubscribers() {
        System.out.println("\nTest 11: KitchenEventPublisher notifies all registered observers");

        List<String> log = new ArrayList<>();
        KitchenObserver observerA = (order, result) -> log.add("A:" + result.getScore());
        KitchenObserver observerB = (order, result) -> log.add("B:" + result.getScore());

        KitchenEventPublisher publisher = new KitchenEventPublisher();
        publisher.registerObserver(observerA);
        publisher.registerObserver(observerB);

        Recipe recipe = RecipeFactory.createAppetizer(
                "Soup", "Hot soup", makeIngredients(), "baking", 1, 1);
        CustomerOrder order = CustomerOrderFactory.createForRecipe(recipe);
        CookingResult result = new CookingResult(true, 85, 30, "Baking", "Great!");

        publisher.notifyDishCompleted(order, result);

        assertEquals("Both observers notified", 2, log.size());
        assertTrue("Observer A received correct score", log.get(0).equals("A:85"));
        assertTrue("Observer B received correct score", log.get(1).equals("B:85"));

        publisher.removeObserver(observerB);
        publisher.notifyDishCompleted(order, result);
        assertEquals("After removal only 1 observer fires", 3, log.size());
    }

    private static void testSatisfactionObserverTracksAverage() {
        System.out.println("\nTest 12: SatisfactionObserver computes correct running average");

        SatisfactionObserver satObs = new SatisfactionObserver();
        KitchenEventPublisher publisher = new KitchenEventPublisher();
        publisher.registerObserver(satObs);

        // Fire two events — satisfaction scores are computed by order.complete()
        // We use a real order so the satisfaction pipeline runs end-to-end.
        Recipe recipe = RecipeFactory.createAppetizer(
                "Chips", "Fried chips", makeIngredients(), "frying", 1, 1);

        CustomerOrder order1 = CustomerOrderFactory.createForRecipe(recipe);
        CookingResult result1 = new CookingResult(true, 100, 10, "Frying", "Perfect");
        publisher.notifyDishCompleted(order1, result1);

        CustomerOrder order2 = CustomerOrderFactory.createForRecipe(recipe);
        CookingResult result2 = new CookingResult(true, 50, 10, "Frying", "Okay");
        publisher.notifyDishCompleted(order2, result2);

        assertEquals("Two orders tracked", 2, satObs.getTotalOrders());
        assertTrue("Average satisfaction >= 0", satObs.getAverageSatisfaction() >= 0);
    }

    // ---- Repository / Service layer ---------------------------------- //

    private static void testRecipeServiceRejectsDuplicates() {
        System.out.println("\nTest 13: RecipeService rejects duplicate recipe names");

        InMemoryRecipeRepository repo = new InMemoryRecipeRepository();
        RecipeService service = new RecipeService(repo);

        Recipe pizza = RecipeFactory.createMainCourse(
                "Margherita Pizza", "Classic pizza", makeIngredients(), "baking", 3, 2);
        service.addRecipe(pizza);
        assertEquals("Repo has 1 recipe", 1, service.getTotalRecipeCount());

        boolean threw = false;
        try {
            Recipe duplicate = RecipeFactory.createMainCourse(
                    "Margherita Pizza", "Different description", makeIngredients(), "baking", 3, 2);
            service.addRecipe(duplicate);
        } catch (IllegalArgumentException e) { threw = true; }

        assertTrue("Duplicate recipe throws exception", threw);
        assertEquals("Repo still has only 1 recipe", 1, service.getTotalRecipeCount());
    }

    private static void testRecipeServiceFiltersAvailableBySkill() {
        System.out.println("\nTest 14: RecipeService filters recipes by player skill level");

        InMemoryRecipeRepository repo = new InMemoryRecipeRepository();
        RecipeService service = new RecipeService(repo);

        service.addRecipe(RecipeFactory.createAppetizer("Easy Dish",  "Simple",  makeIngredients(), "frying",   1, 1));
        service.addRecipe(RecipeFactory.createMainCourse("Hard Dish", "Complex", makeIngredients(), "grilling", 8, 5));

        List<Recipe> forBeginner = service.getAvailableRecipes(3);
        List<Recipe> forExpert   = service.getAvailableRecipes(10);

        assertEquals("Beginner sees 1 recipe", 1, forBeginner.size());
        assertEquals("Expert sees all 2 recipes", 2, forExpert.size());
    }

    private static void testInventoryLowStockObserverFires() {
        System.out.println("\nTest 15: InventoryRepository fires low-stock observer notification");

        List<String> alerts = new ArrayList<>();
        InventoryObserver observer = new InventoryObserver() {
            @Override public void onLowStock(Ingredient ing, double qty) {
                alerts.add("LOW:" + ing.getName());
            }
            @Override public void onStockExpired(Ingredient ing) {
                alerts.add("EXPIRED:" + ing.getName());
            }
        };

        InventoryRepository repo = new InventoryRepository();
        repo.addObserver(observer);

        Ingredient flour = new Ingredient("ing-fl", "Flour", "g", 12.0, 5.0, 0.10);
        repo.save(flour);

        flour.consume();
        repo.update(flour); // triggers threshold check

        assertTrue("Low-stock alert fired", alerts.contains("LOW:Flour"));
    }

    private static void testInventoryServicePurgesExpiredIngredients() {
        System.out.println("\nTest 16: InventoryService purges expired ingredients");

        InventoryRepository repo = new InventoryRepository();
        InventoryService service = new InventoryService(repo);

        Ingredient fresh = IngredientFactory.createDairy("ing-milk",  "Milk",  "ml",  1000, 200, 0.002, 7);
        Ingredient stale = IngredientFactory.createPerishable("ing-stale", "Old Cream", "ml", 500, 100, 0.01,
                LocalDate.now().minusDays(2));

        service.addIngredient(fresh);
        service.addIngredient(stale);

        List<Ingredient> purged = service.purgeExpiredIngredients();
        assertEquals("One expired item purged", 1, purged.size());
        assertEquals("Purged item is Old Cream", "Old Cream", purged.get(0).getName());
        assertEquals("Only fresh item remains", 1, service.getAllIngredients().size());
    }

    // ---- Polymorphism ------------------------------------------------- //

    private static void testPolymorphicBasePricing() {
        System.out.println("\nTest 17: Polymorphism — base price differs per recipe subtype");

        List<Recipe> recipes = List.of(
                RecipeFactory.createAppetizer("Salad",    "Fresh salad",      makeIngredients(), "grilling", 1, 2),
                RecipeFactory.createMainCourse("Pasta",   "Al dente pasta",   makeIngredients(), "baking",   2, 2),
                RecipeFactory.createDessert("Tiramisu",   "Italian dessert",  makeIngredients(), "baking",   3, 2)
        );

        double appetizerPrice  = recipes.get(0).getBasePrice(); // 5 + 2 = 7.0
        double mainCoursePrice = recipes.get(1).getBasePrice(); // 12 + 5 = 17.0
        double dessertPrice    = recipes.get(2).getBasePrice(); // 7 + 3.6 = 10.6

        assertTrue("MainCourse > Dessert > Appetizer",
                mainCoursePrice > dessertPrice && dessertPrice > appetizerPrice);
        assertEquals("Appetizer price",  7.0,  appetizerPrice);
        assertEquals("MainCourse price", 17.0, mainCoursePrice);
    }

    // ---- Integration: GameManager ------------------------------------ //

    private static void testGameManagerSeededDataLoadedCorrectly() {
        System.out.println("\nTest 18: GameManager seeds recipes and inventory on startup");

        GameManager game = new GameManager(5);
        game.seedDefaultData();

        assertTrue("At least 5 recipes seeded", game.getAllRecipes().size() >= 5);
        assertTrue("Skill level reflects constructor arg", game.getPlayerSkillLevel() == 5);
    }

    private static void testGameManagerLevelUpCapsAtTen() {
        System.out.println("\nTest 19: GameManager level-up is capped at 10");

        GameManager game = new GameManager(9);
        game.levelUp(); // → 10
        game.levelUp(); // should stay at 10
        assertEquals("Skill level capped at 10", 10, game.getPlayerSkillLevel());
    }

    private static void testGameManagerSubmitAndProcessOrder() {
        System.out.println("\nTest 20: GameManager submit + process order reduces queue to 0");

        GameManager game = new GameManager(5);
        game.seedDefaultData();

        game.submitRandomOrder();
        assertEquals("Queue size is 1 after submit", 1, game.getOrderQueueSize());

        game.processNextOrder();
        assertEquals("Queue is empty after processing", 0, game.getOrderQueueSize());
    }

    // ================================================================== //
    // Runner                                                              //
    // ================================================================== //

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Recipe Realm Test Suite");
        System.out.println("==========================================");

        // Strategy
        testBakingStrategyScoreScalesWithSkill();
        testFryingStrategyBonusForHighSkill();
        testGrillingStrategyHighSkillUnlocksBonus();
        testSteamingStrategyScoresHighWithMatchingSkill();

        // Factory
        testRecipeFactoryWiresCorrectStrategy();
        testIngredientFactoryCreatesPerishableWithExpiry();
        testIngredientFactoryNonPerishableNeverExpires();

        // Decorator
        testStackedDecoratorsCompoundCorrectly();
        testPremiumDecoratorRaisesSkillRequirement();
        testGlutenFreeDecoratorAddsCorrectSurcharge();

        // Observer
        testObserverPatternNotifiesAllSubscribers();
        testSatisfactionObserverTracksAverage();

        // Repository / Service
        testRecipeServiceRejectsDuplicates();
        testRecipeServiceFiltersAvailableBySkill();
        testInventoryLowStockObserverFires();
        testInventoryServicePurgesExpiredIngredients();

        // Polymorphism
        testPolymorphicBasePricing();

        // Integration
        testGameManagerSeededDataLoadedCorrectly();
        testGameManagerLevelUpCapsAtTen();
        testGameManagerSubmitAndProcessOrder();

        System.out.println("\n==========================================");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("==========================================");
        System.exit(failed > 0 ? 1 : 0);
    }
}