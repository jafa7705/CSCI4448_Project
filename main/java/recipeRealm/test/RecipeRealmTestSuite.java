package recipeRealm.test;

import recipeRealm.cooking.*;
import recipeRealm.decorator.*;
import recipeRealm.factory.*;
import recipeRealm.model.*;
import recipeRealm.observer.*;
import recipeRealm.repository.*;
import recipeRealm.service.*;

import java.util.ArrayList;
import java.util.List;


public class RecipeRealmTestSuite {

    private static int passed = 0, failed = 0;

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

    private static List<Ingredient> makeIngredients() {
        Ingredient egg = new Ingredient("ing-001", "Egg", "pieces", 10, 2, 0.30);
        List<Ingredient> list = new ArrayList<>();
        list.add(egg);
        return list;
    }

    private static void testBakingStrategyScoreScalesWithSkill() {
        System.out.println("\nTest 1: BakingStrategy scores scale with skill level");
        BakingStrategy baking = new BakingStrategy();
        Recipe recipe = RecipeFactory.createDessert(
                "Croissant", "Buttery pastry", makeIngredients(), "baking", 6, 3);

        CookingResult expertResult = baking.cook(recipe, 10);   // expert
        CookingResult noviceResult = baking.cook(recipe, 1);    // novice

        assertTrue("Expert score >= 90", expertResult.getScore() >= 90);
        assertTrue("Novice score < expert score", noviceResult.getScore() < expertResult.getScore());
        assertTrue("Expert attempt is successful", expertResult.isSuccess());
    }

    private static void testFryingStrategyBonusForHighSkill() {
        System.out.println("\nTest 2: FryingStrategy grants a bonus to skilled players");
        FryingStrategy frying = new FryingStrategy();
        Recipe easyRecipe = RecipeFactory.createAppetizer(
                "Spring Rolls", "Crispy rolls", makeIngredients(), "frying", 2, 1);

        CookingResult highSkill = frying.cook(easyRecipe, 9);
        CookingResult lowSkill  = frying.cook(easyRecipe, 2);

        assertTrue("High-skill score > low-skill score", highSkill.getScore() > lowSkill.getScore());
        assertTrue("Cooking method is 'Frying'", "Frying".equals(highSkill.getCookingMethod()));
        assertTrue("Time taken is positive", highSkill.getTimeTakenSeconds() > 0);
    }

    private static void testRecipeFactoryWiresCorrectStrategy() {
        System.out.println("\nTest 3: RecipeFactory wires the correct CookingStrategy");
        Recipe steak = RecipeFactory.createMainCourse(
                "Ribeye Steak", "Prime cut", makeIngredients(), "grilling", 5, 3);

        CookingStrategy strategy = steak.getCookingStrategy();
        assertEquals("Strategy name is 'Grilling'", "Grilling", strategy.getMethodName());
        assertTrue("Base time > 0", strategy.getBaseTimeSeconds() > 0);

        boolean threw = false;
        try { RecipeFactory.createMainCourse("X", "X", makeIngredients(), "steaming", 1, 1); }
        catch (IllegalArgumentException e) { threw = true; }
        assertTrue("Factory throws on unknown cooking method", threw);
    }

    private static void testStackedDecoratorsCompoundCorrectly() {
        System.out.println("\nTest 4: Stacked decorators compound price and name");
        Recipe base = RecipeFactory.createMainCourse(
                "Burger", "Classic beef burger", makeIngredients(), "grilling", 3, 2);

        double basePrice = base.getBasePrice(); // 12 + 2.5*2 = 17.0

        Recipe spicy  = new ExtraSpicyDecorator(base);
        Recipe doubled = new DoubleServingDecorator(spicy);

        double expectedPrice = (basePrice + 2.0) * 2.0;
        assertTrue("Price compounds correctly",
                Math.abs(doubled.getBasePrice() - expectedPrice) < 0.001);
        assertTrue("Name includes both decorations",
                doubled.getName().contains("Extra Spicy") && doubled.getName().contains("Double"));
        assertTrue("Category unchanged by decorators", "MainCourse".equals(doubled.getCategory()));
    }

    private static void testObserverPatternNotifiesAllSubscribers() {
        System.out.println("\nTest 5: KitchenEventPublisher notifies all registered observers");

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

    private static void testRecipeServiceRejectsDuplicates() {
        System.out.println("\nTest 6: RecipeService rejects duplicate recipe names");

        InMemoryRecipeRepository repo = new InMemoryRecipeRepository();
        RecipeService service = new RecipeService(repo); // dependency injected

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

    private static void testPolymorphicBasePricing() {
        System.out.println("\nTest 7: Polymorphism — base price differs per recipe subtype");

        List<Recipe> recipes = List.of(
                RecipeFactory.createAppetizer("Salad", "Fresh salad", makeIngredients(), "grilling", 1, 2),
                RecipeFactory.createMainCourse("Pasta", "Al dente pasta", makeIngredients(), "baking", 2, 2),
                RecipeFactory.createDessert("Tiramisu", "Italian dessert", makeIngredients(), "baking", 3, 2)
        );

        double appetizerPrice  = recipes.get(0).getBasePrice(); // 5 + 2 = 7.0
        double mainCoursePrice = recipes.get(1).getBasePrice(); // 12 + 5 = 17.0
        double dessertPrice    = recipes.get(2).getBasePrice(); // 7 + 3.6 = 10.6

        assertTrue("MainCourse > Dessert > Appetizer",
                mainCoursePrice > dessertPrice && dessertPrice > appetizerPrice);
        assertEquals("Appetizer price", 7.0, appetizerPrice);
        assertEquals("MainCourse price", 17.0, mainCoursePrice);
    }

    private static void testInventoryLowStockObserverFires() {
        System.out.println("\nTest 8: InventoryRepository fires low-stock observer notification");

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
        repo.update(flour);

        assertTrue("Low-stock alert fired", alerts.contains("LOW:Flour"));
    }

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("  Recipe Realm Test Suite");
        System.out.println("======================================");

        testBakingStrategyScoreScalesWithSkill();
        testFryingStrategyBonusForHighSkill();
        testRecipeFactoryWiresCorrectStrategy();
        testStackedDecoratorsCompoundCorrectly();
        testObserverPatternNotifiesAllSubscribers();
        testRecipeServiceRejectsDuplicates();
        testPolymorphicBasePricing();
        testInventoryLowStockObserverFires();

        System.out.println("\n======================================");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("======================================");
        System.exit(failed > 0 ? 1 : 0);
    }
}
