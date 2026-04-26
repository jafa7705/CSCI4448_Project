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
        CookingResult expertResult = baking.cook(recipe, 10);
        CookingResult noviceResult = baking.cook(recipe, 1);
        assertTrue("Expert score >= 90", expertResult.getScore() >= 90);
        assertTrue("Novice score < expert score", noviceResult.getScore() < expertResult.getScore());
        assertTrue("Expert attempt is successful", expertResult.isSuccess());
    }
    private static void testFryingStrategyBonusForHighSkill() {
        System.out.println("\nTest 2: FryingStrategy scores higher when skill exceeds requirement");
        FryingStrategy frying = new FryingStrategy();
        Recipe hardRecipe = RecipeFactory.createMainCourse(
                "Tempura", "Delicate battered fry", makeIngredients(), "frying", 6, 2);
        CookingResult highSkill = frying.cook(hardRecipe, 9);  
        CookingResult lowSkill  = frying.cook(hardRecipe, 3);  
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
    private static void testRecipeFactoryWiresCorrectStrategy() {
        System.out.println("\nTest 5: RecipeFactory wires the correct CookingStrategy");
        Recipe steak = RecipeFactory.createMainCourse(
                "Ribeye Steak", "Prime cut", makeIngredients(), "grilling", 5, 3);
        CookingStrategy strategy = steak.getCookingStrategy();
        assertEquals("Strategy name is 'Grilling'", "Grilling", strategy.getMethodName());
        assertTrue("Base time > 0", strategy.getBaseTimeSeconds() > 0);
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
    private static void testStackedDecoratorsCompoundCorrectly() {
        System.out.println("\nTest 8: Stacked decorators compound price and name");
        Recipe base = RecipeFactory.createMainCourse(
                "Burger", "Classic beef burger", makeIngredients(), "grilling", 3, 2);
        double basePrice = base.getBasePrice(); 
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
        repo.update(flour); 
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
    private static void testPolymorphicBasePricing() {
        System.out.println("\nTest 17: Polymorphism — base price differs per recipe subtype");
        List<Recipe> recipes = List.of(
                RecipeFactory.createAppetizer("Salad",    "Fresh salad",      makeIngredients(), "grilling", 1, 2),
                RecipeFactory.createMainCourse("Pasta",   "Al dente pasta",   makeIngredients(), "baking",   2, 2),
                RecipeFactory.createDessert("Tiramisu",   "Italian dessert",  makeIngredients(), "baking",   3, 2)
        );
        double appetizerPrice  = recipes.get(0).getBasePrice(); 
        double mainCoursePrice = recipes.get(1).getBasePrice(); 
        double dessertPrice    = recipes.get(2).getBasePrice(); 
        assertTrue("MainCourse > Dessert > Appetizer",
                mainCoursePrice > dessertPrice && dessertPrice > appetizerPrice);
        assertEquals("Appetizer price",  7.0,  appetizerPrice);
        assertEquals("MainCourse price", 17.0, mainCoursePrice);
    }
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
        game.levelUp(); 
        game.levelUp(); 
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
    private static void testIngredientConsumeReducesQuantity() {
        System.out.println("\nTest 21: Ingredient.consume() reduces quantity by requiredAmount");
        Ingredient ing = new Ingredient("i1", "Salt", "g", 100, 10, 0.01);
        ing.consume();
        assertEquals("Quantity after consume", 90.0, ing.getQuantity());
    }
    private static void testIngredientConsumeThrowsWhenUnavailable() {
        System.out.println("\nTest 22: Ingredient.consume() throws when quantity < requiredAmount");
        Ingredient ing = new Ingredient("i2", "Egg", "pieces", 1, 5, 0.30);
        boolean threw = false;
        try { ing.consume(); } catch (IllegalStateException e) { threw = true; }
        assertTrue("consume() throws when insufficient", threw);
    }
    private static void testIngredientRestockIncreasesQuantity() {
        System.out.println("\nTest 23: Ingredient.restock() increases quantity");
        Ingredient ing = new Ingredient("i3", "Flour", "g", 50, 100, 0.002);
        ing.restock(200);
        assertEquals("Quantity after restock", 250.0, ing.getQuantity());
    }
    private static void testIngredientRestockThrowsOnNegative() {
        System.out.println("\nTest 24: Ingredient.restock() throws on non-positive amount");
        Ingredient ing = new Ingredient("i4", "Sugar", "g", 100, 10, 0.003);
        boolean threw = false;
        try { ing.restock(-5); } catch (IllegalArgumentException e) { threw = true; }
        assertTrue("restock() throws on negative", threw);
    }
    private static void testIngredientToString() {
        System.out.println("\nTest 25: Ingredient.toString() contains name");
        Ingredient ing = new Ingredient("i5", "Butter", "g", 100, 50, 0.01);
        assertTrue("toString contains name", ing.toString().contains("Butter"));
    }
    private static void testIngredientSetters() {
        System.out.println("\nTest 26: Ingredient setExpiryDate and setCostPerUnit work");
        Ingredient ing = new Ingredient("i6", "Milk", "ml", 500, 100, 0.002);
        ing.setCostPerUnit(0.005);
        ing.setExpiryDate(java.time.LocalDate.now().plusDays(3));
        assertEquals("CostPerUnit updated", 0.005, ing.getCostPerUnit());
        assertTrue("ExpiryDate set", ing.getExpiryDate() != null);
    }
    private static void testCustomerOrderComplete() {
        System.out.println("\nTest 27: CustomerOrder.complete() sets COMPLETED status on success");
        Recipe recipe = RecipeFactory.createAppetizer("Soup", "Hot soup", makeIngredients(), "baking", 1, 1);
        CustomerOrder order = CustomerOrderFactory.createForRecipe(recipe);
        CookingResult result = new CookingResult(true, 80, 30, "Baking", "Good");
        order.complete(result);
        assertEquals("Status is COMPLETED", CustomerOrder.Status.COMPLETED, order.getStatus());
        assertTrue("Satisfaction > 0", order.getSatisfactionScore() > 0);
    }
    private static void testCustomerOrderFailedStatus() {
        System.out.println("\nTest 28: CustomerOrder.complete() sets FAILED on unsuccessful result");
        Recipe recipe = RecipeFactory.createAppetizer("Salad", "Fresh", makeIngredients(), "grilling", 1, 1);
        CustomerOrder order = CustomerOrderFactory.createForRecipe(recipe);
        CookingResult result = new CookingResult(false, 20, 30, "Grilling", "Bad");
        order.complete(result);
        assertEquals("Status is FAILED", CustomerOrder.Status.FAILED, order.getStatus());
    }
    private static void testCustomerOrderGetters() {
        System.out.println("\nTest 29: CustomerOrder getters return correct values");
        Recipe recipe = RecipeFactory.createAppetizer("Chips", "Fried", makeIngredients(), "frying", 1, 1);
        CustomerOrder order = CustomerOrderFactory.createForRecipe(recipe);
        assertTrue("OrderId not null", order.getOrderId() != null);
        assertTrue("CustomerId not null", order.getCustomerId() != null);
        assertTrue("CreatedAt not null", order.getCreatedAt() != null);
        assertTrue("PatienceSeconds > 0", order.getPatienceSeconds() > 0);
        assertEquals("Initial status PENDING", CustomerOrder.Status.PENDING, order.getStatus());
    }
    private static void testCookingResultGetters() {
        System.out.println("\nTest 30: CookingResult getters return correct values");
        CookingResult r = new CookingResult(true, 75, 45, "Frying", "Nice!");
        assertTrue("isSuccess true", r.isSuccess());
        assertEquals("Score", 75, r.getScore());
        assertEquals("TimeTaken", 45, r.getTimeTakenSeconds());
        assertEquals("Method", "Frying", r.getCookingMethod());
        assertEquals("Feedback", "Nice!", r.getFeedbackMessage());
        assertTrue("Stars > 0", r.getStarRating() > 0);
        assertTrue("toString works", r.toString().contains("Frying"));
    }
    private static void testCookingResultStarRatings() {
        System.out.println("\nTest 31: CookingResult star rating scales with score");
        assertEquals("Score 100 = 5 stars", 5, new CookingResult(true, 100, 10, "X", "").getStarRating());
        assertEquals("Score 80 = 4 stars",  4, new CookingResult(true, 80,  10, "X", "").getStarRating());
        assertEquals("Score 60 = 3 stars",  3, new CookingResult(true, 60,  10, "X", "").getStarRating());
        assertEquals("Score 30 = 2 stars",  2, new CookingResult(false, 45, 10, "X", "").getStarRating());
        assertEquals("Score 10 = 1 star",   1, new CookingResult(false, 10, 10, "X", "").getStarRating());
    }
    private static void testBakingStrategyLowSkillFeedback() {
        System.out.println("\nTest 32: BakingStrategy gives low score for large skill gap");
        recipeRealm.cooking.BakingStrategy baking = new recipeRealm.cooking.BakingStrategy();
        Recipe hardRecipe = RecipeFactory.createDessert("Croissant", "Pastry", makeIngredients(), "baking", 10, 5);
        CookingResult result = baking.cook(hardRecipe, 1);
        assertTrue("Score low for huge skill gap", result.getScore() < 60);
    }
    private static void testGrillingStrategyLowSkillFeedback() {
        System.out.println("\nTest 33: GrillingStrategy gives lower score for low skill");
        recipeRealm.cooking.GrillingStrategy grilling = new recipeRealm.cooking.GrillingStrategy();
        Recipe hardRecipe = RecipeFactory.createMainCourse("Wagyu", "Expensive", makeIngredients(), "grilling", 10, 5);
        CookingResult expertResult = grilling.cook(hardRecipe, 10);
        CookingResult noviceResult = grilling.cook(hardRecipe, 1);
        assertTrue("Novice scores lower than expert", noviceResult.getScore() < expertResult.getScore());
    }
    private static void testFryingStrategyLowSkillFeedback() {
        System.out.println("\nTest 34: FryingStrategy gives soggy feedback for low skill");
        recipeRealm.cooking.FryingStrategy frying = new recipeRealm.cooking.FryingStrategy();
        Recipe hardRecipe = RecipeFactory.createMainCourse("Tempura", "Battered", makeIngredients(), "frying", 10, 5);
        CookingResult result = frying.cook(hardRecipe, 1);
        assertTrue("Low skill frying fails", !result.isSuccess());
    }
    private static void testSteamingStrategyLowSkillFeedback() {
        System.out.println("\nTest 35: SteamingStrategy gives bland feedback for low skill");
        recipeRealm.cooking.SteamingStrategy steaming = new recipeRealm.cooking.SteamingStrategy();
        Recipe hardRecipe = RecipeFactory.createMainCourse("Sea Bass", "Steamed", makeIngredients(), "steaming", 10, 5);
        CookingResult result = steaming.cook(hardRecipe, 1);
        assertTrue("Low skill steaming score < 70", result.getScore() < 70);
    }
    private static void testInMemoryRecipeRepositoryUpdate() {
        System.out.println("\nTest 36: InMemoryRecipeRepository.update() replaces existing recipe");
        recipeRealm.repository.InMemoryRecipeRepository repo = new recipeRealm.repository.InMemoryRecipeRepository();
        Recipe r = RecipeFactory.createAppetizer("Soup", "Hot", makeIngredients(), "baking", 1, 1);
        repo.save(r);
        repo.update(r); 
        assertEquals("Still 1 recipe after update", 1, repo.size());
    }
    private static void testInMemoryRecipeRepositoryUpdateThrowsOnMissing() {
        System.out.println("\nTest 37: InMemoryRecipeRepository.update() throws if not found");
        recipeRealm.repository.InMemoryRecipeRepository repo = new recipeRealm.repository.InMemoryRecipeRepository();
        Recipe r = RecipeFactory.createAppetizer("Ghost", "Missing", makeIngredients(), "baking", 1, 1);
        boolean threw = false;
        try { repo.update(r); } catch (java.util.NoSuchElementException e) { threw = true; }
        assertTrue("update() throws when recipe not in repo", threw);
    }
    private static void testInMemoryRecipeRepositoryFindByCategory() {
        System.out.println("\nTest 38: InMemoryRecipeRepository.findByCategory() filters correctly");
        recipeRealm.repository.InMemoryRecipeRepository repo = new recipeRealm.repository.InMemoryRecipeRepository();
        repo.save(RecipeFactory.createAppetizer("A1", "d", makeIngredients(), "baking", 1, 1));
        repo.save(RecipeFactory.createMainCourse("M1", "d", makeIngredients(), "baking", 1, 1));
        assertEquals("One appetizer", 1, repo.findByCategory("Appetizer").size());
        assertEquals("One main course", 1, repo.findByCategory("MainCourse").size());
    }
    private static void testInventoryRepositoryFindById() {
        System.out.println("\nTest 39: InventoryRepository.findById() returns correct ingredient");
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        Ingredient ing = new Ingredient("x1", "Pepper", "g", 100, 5, 0.01);
        repo.save(ing);
        assertTrue("findById returns ingredient", repo.findById("x1").isPresent());
        assertTrue("findById missing returns empty", repo.findById("nope").isEmpty());
    }
    private static void testInventoryRepositoryDelete() {
        System.out.println("\nTest 40: InventoryRepository.delete() removes ingredient");
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        Ingredient ing = new Ingredient("d1", "Thyme", "g", 50, 5, 0.01);
        repo.save(ing);
        repo.delete("d1");
        assertTrue("Ingredient removed after delete", repo.findById("d1").isEmpty());
    }
    private static void testInventoryRepositoryConsumeThrowsOnMissing() {
        System.out.println("\nTest 41: InventoryRepository.consumeIngredients() throws for missing ingredient");
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        Ingredient missing = new Ingredient("miss", "Ghost", "g", 10, 5, 0.01);
        boolean threw = false;
        try { repo.consumeIngredients(List.of(missing)); } catch (java.util.NoSuchElementException e) { threw = true; }
        assertTrue("consumeIngredients throws on missing", threw);
    }
    private static void testInventoryRepositoryConsumeThrowsOnExpired() {
        System.out.println("\nTest 42: InventoryRepository.consumeIngredients() throws for expired ingredient");
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        Ingredient expired = IngredientFactory.createPerishable("exp1", "OldMilk", "ml", 500, 100, 0.002,
                java.time.LocalDate.now().minusDays(1));
        repo.save(expired);
        boolean threw = false;
        try { repo.consumeIngredients(List.of(expired)); } catch (IllegalStateException e) { threw = true; }
        assertTrue("consumeIngredients throws on expired", threw);
    }
    private static void testInventoryRepositoryFindLowStock() {
        System.out.println("\nTest 43: InventoryRepository.findLowStock() returns low stock items");
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        Ingredient low = new Ingredient("low1", "Saffron", "g", 1, 5, 1.0);
        Ingredient fine = new Ingredient("ok1", "Rice", "g", 1000, 100, 0.001);
        repo.save(low); repo.save(fine);
        List<Ingredient> lowStock = repo.findLowStock();
        assertTrue("Low stock item found", lowStock.stream().anyMatch(i -> i.getName().equals("Saffron")));
        assertTrue("Fine item not flagged", lowStock.stream().noneMatch(i -> i.getName().equals("Rice")));
    }
    private static void testInventoryServiceRestockIngredient() {
        System.out.println("\nTest 44: InventoryService.restockIngredient() increases stock");
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        recipeRealm.service.InventoryService service = new recipeRealm.service.InventoryService(repo);
        Ingredient ing = new Ingredient("r1", "Yeast", "g", 20, 10, 0.05);
        service.addIngredient(ing);
        service.restockIngredient("r1", 50);
        assertEquals("Stock increased", 70.0, service.getAllIngredients().get(0).getQuantity());
    }
    private static void testInventoryServiceRestockThrowsOnMissing() {
        System.out.println("\nTest 45: InventoryService.restockIngredient() throws when not found");
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        recipeRealm.service.InventoryService service = new recipeRealm.service.InventoryService(repo);
        boolean threw = false;
        try { service.restockIngredient("nope", 10); } catch (java.util.NoSuchElementException e) { threw = true; }
        assertTrue("restock throws on missing ingredient", threw);
    }
    private static void testInventoryServiceHasAllIngredients() {
        System.out.println("\nTest 46: InventoryService.hasAllIngredients() returns true when stocked");
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        recipeRealm.service.InventoryService service = new recipeRealm.service.InventoryService(repo);
        Ingredient ing = new Ingredient("h1", "Oil", "ml", 500, 30, 0.005);
        service.addIngredient(ing);
        assertTrue("Has ingredient", service.hasAllIngredients(List.of(ing)));
        Ingredient missing = new Ingredient("h2", "Truffle", "g", 0, 10, 10.0);
        assertTrue("Missing returns false", !service.hasAllIngredients(List.of(missing)));
    }
    private static void testInventoryServiceGetLowStock() {
        System.out.println("\nTest 47: InventoryService.getLowStockIngredients() delegates correctly");
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        recipeRealm.service.InventoryService service = new recipeRealm.service.InventoryService(repo);
        Ingredient low = new Ingredient("ls1", "Vanilla", "g", 1, 10, 0.5);
        service.addIngredient(low);
        assertTrue("Low stock returned", !service.getLowStockIngredients().isEmpty());
    }
    private static void testOrderServiceHasOrders() {
        System.out.println("\nTest 48: OrderService.hasOrders() reflects queue state");
        recipeRealm.observer.KitchenEventPublisher pub = new recipeRealm.observer.KitchenEventPublisher();
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        Ingredient ing = new Ingredient("oi1", "Egg", "pieces", 100, 2, 0.30);
        repo.save(ing);
        recipeRealm.service.OrderService svc = new recipeRealm.service.OrderService(pub, repo);
        assertTrue("Empty queue = no orders", !svc.hasOrders());
        Recipe recipe = RecipeFactory.createAppetizer("Omelette", "Egg dish",
                List.of(new Ingredient("oi1", "Egg", "pieces", 100, 2, 0.30)), "frying", 1, 1);
        CustomerOrder order = CustomerOrderFactory.createForRecipe(recipe);
        svc.enqueueOrder(order);
        assertTrue("Has orders after enqueue", svc.hasOrders());
    }
    private static void testOrderServiceGetCompletedOrders() {
        System.out.println("\nTest 49: OrderService.getCompletedOrders() grows after processing");
        recipeRealm.observer.KitchenEventPublisher pub = new recipeRealm.observer.KitchenEventPublisher();
        recipeRealm.repository.InventoryRepository repo = new recipeRealm.repository.InventoryRepository();
        Ingredient ing = new Ingredient("co1", "Egg", "pieces", 100, 2, 0.30);
        repo.save(ing);
        recipeRealm.service.OrderService svc = new recipeRealm.service.OrderService(pub, repo);
        Recipe recipe = RecipeFactory.createAppetizer("Scrambled", "Eggs",
                List.of(new Ingredient("co1", "Egg", "pieces", 100, 2, 0.30)), "baking", 1, 1);
        svc.enqueueOrder(CustomerOrderFactory.createForRecipe(recipe));
        svc.processNextOrder(5);
        assertEquals("One completed order", 1, svc.getCompletedOrders().size());
    }
    private static void testRecipeServiceFindById() {
        System.out.println("\nTest 50: RecipeService.findById() returns correct recipe");
        recipeRealm.repository.InMemoryRecipeRepository repo = new recipeRealm.repository.InMemoryRecipeRepository();
        recipeRealm.service.RecipeService service = new recipeRealm.service.RecipeService(repo);
        Recipe r = RecipeFactory.createAppetizer("Beet Salad", "Fresh beets", makeIngredients(), "grilling", 1, 1);
        service.addRecipe(r);
        assertTrue("findById returns recipe", service.findById(r.getId()).isPresent());
        assertTrue("findById missing returns empty", service.findById("nope").isEmpty());
    }
    private static void testRecipeServiceGetByCategory() {
        System.out.println("\nTest 51: RecipeService.getByCategory() filters correctly");
        recipeRealm.repository.InMemoryRecipeRepository repo = new recipeRealm.repository.InMemoryRecipeRepository();
        recipeRealm.service.RecipeService service = new recipeRealm.service.RecipeService(repo);
        service.addRecipe(RecipeFactory.createDessert("Cake", "Sweet", makeIngredients(), "baking", 1, 1));
        service.addRecipe(RecipeFactory.createAppetizer("Bruschetta2", "Crunchy", makeIngredients(), "grilling", 1, 1));
        assertEquals("One dessert", 1, service.getByCategory("Dessert").size());
        assertEquals("One appetizer", 1, service.getByCategory("Appetizer").size());
    }
    private static void testRecipeServiceRemove() {
        System.out.println("\nTest 52: RecipeService.removeRecipe() removes by id");
        recipeRealm.repository.InMemoryRecipeRepository repo = new recipeRealm.repository.InMemoryRecipeRepository();
        recipeRealm.service.RecipeService service = new recipeRealm.service.RecipeService(repo);
        Recipe r = RecipeFactory.createAppetizer("Tempura2", "Fried", makeIngredients(), "frying", 1, 1);
        service.addRecipe(r);
        service.removeRecipe(r.getId());
        assertEquals("Repo empty after remove", 0, service.getTotalRecipeCount());
    }
    private static void testRecipeServiceRemoveThrowsOnMissing() {
        System.out.println("\nTest 53: RecipeService.removeRecipe() throws when not found");
        recipeRealm.repository.InMemoryRecipeRepository repo = new recipeRealm.repository.InMemoryRecipeRepository();
        recipeRealm.service.RecipeService service = new recipeRealm.service.RecipeService(repo);
        boolean threw = false;
        try { service.removeRecipe("nonexistent"); } catch (java.util.NoSuchElementException e) { threw = true; }
        assertTrue("removeRecipe throws on missing", threw);
    }
    private static void testKitchenEventPublisherIgnoresNullAndDuplicates() {
        System.out.println("\nTest 54: KitchenEventPublisher ignores null and duplicate registrations");
        recipeRealm.observer.KitchenEventPublisher pub = new recipeRealm.observer.KitchenEventPublisher();
        List<Integer> count = new ArrayList<>();
        recipeRealm.observer.KitchenObserver obs = (o, r) -> count.add(1);
        pub.registerObserver(obs);
        pub.registerObserver(obs);   
        pub.registerObserver(null);  
        Recipe recipe = RecipeFactory.createAppetizer("X", "X", makeIngredients(), "baking", 1, 1);
        pub.notifyDishCompleted(CustomerOrderFactory.createForRecipe(recipe),
                new CookingResult(true, 80, 10, "Baking", "OK"));
        assertEquals("Observer fired exactly once", 1, count.size());
    }
    private static void testStockAlertObserverCollectsAndClears() {
        System.out.println("\nTest 55: StockAlertObserver collects alerts and clears them");
        recipeRealm.observer.StockAlertObserver obs = new recipeRealm.observer.StockAlertObserver();
        Ingredient ing = new Ingredient("sa1", "Salt", "g", 10, 5, 0.01);
        obs.onLowStock(ing, 10);
        obs.onStockExpired(ing);
        assertEquals("Two alerts collected", 2, obs.getAlerts().size());
        obs.clearAlerts();
        assertEquals("Alerts cleared", 0, obs.getAlerts().size());
    }
    private static void testOrderFulfillmentObserverSpend() {
        System.out.println("\nTest 56: OrderFulfillmentObserver.spend() reduces earnings");
        recipeRealm.observer.OrderFulfillmentObserver obs = new recipeRealm.observer.OrderFulfillmentObserver();
        Recipe recipe = RecipeFactory.createMainCourse("Steak", "Beef", makeIngredients(), "grilling", 3, 2);
        CustomerOrder order = CustomerOrderFactory.createForRecipe(recipe);
        CookingResult result = new CookingResult(true, 100, 30, "Grilling", "Perfect");
        obs.onDishCompleted(order, result);
        double before = obs.getTotalEarnings();
        obs.spend(5.0);
        assertTrue("Spend reduces earnings", obs.getTotalEarnings() < before);
    }
    private static void testGameManagerGetters() {
        System.out.println("\nTest 57: GameManager getters return correct values after seeding");
        GameManager game = new GameManager(4);
        game.seedDefaultData();
        assertTrue("Total earnings starts at 0", game.getTotalEarnings() == 0.0);
        assertEquals("Average satisfaction starts at 0", 0, game.getAverageSatisfaction());
        assertEquals("Queue starts empty", 0, game.getOrderQueueSize());
        assertTrue("Recipes available", game.getAllRecipes().size() > 0);
    }
    private static void testGameManagerSpendFunds() {
        System.out.println("\nTest 58: GameManager.spendFunds() reduces total earnings");
        GameManager game = new GameManager(5);
        game.seedDefaultData();
        game.submitRandomOrder();
        game.processNextOrder();
        double after = game.getTotalEarnings();
        game.spendFunds(1.0);
        assertTrue("spendFunds reduces earnings", game.getTotalEarnings() < after);
    }
    private static void testGameManagerLevelUpAtMaxDoesNothing() {
        System.out.println("\nTest 59: GameManager.levelUp() at max level 10 stays at 10");
        GameManager game = new GameManager(10);
        game.levelUp();
        game.levelUp();
        assertEquals("Still at 10", 10, game.getPlayerSkillLevel());
    }
    private static void testExtraSpicyDecoratorAlone() {
        System.out.println("\nTest 60: ExtraSpicyDecorator increases complexity by 1");
        Recipe base = RecipeFactory.createAppetizer("Wings", "Spicy wings", makeIngredients(), "frying", 2, 2);
        Recipe spicy = new recipeRealm.decorator.ExtraSpicyDecorator(base);
        assertEquals("Complexity +1", base.getComplexity() + 1, spicy.getComplexity());
        assertTrue("Name contains Extra Spicy", spicy.getName().contains("Extra Spicy"));
    }
    private static void testDoubleServingDecoratorAlone() {
        System.out.println("\nTest 61: DoubleServingDecorator doubles prep time");
        Recipe base = RecipeFactory.createMainCourse("Pasta", "Carbonara", makeIngredients(), "baking", 3, 2);
        Recipe doubled = new recipeRealm.decorator.DoubleServingDecorator(base);
        assertEquals("Prep time doubled", base.getPreparationTimeSeconds() * 2, doubled.getPreparationTimeSeconds());
        assertTrue("Name contains Double", doubled.getName().contains("Double"));
    }
    private static void testExtraSpicyComplexityCappedAt5() {
        System.out.println("\nTest 62: ExtraSpicyDecorator caps complexity at 5");
        Recipe base = RecipeFactory.createMainCourse("Extreme", "Hard", makeIngredients(), "grilling", 5, 5);
        Recipe spicy = new recipeRealm.decorator.ExtraSpicyDecorator(base);
        assertEquals("Complexity capped at 5", 5, spicy.getComplexity());
    }
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Recipe Realm Test Suite");
        System.out.println("==========================================");
        testBakingStrategyScoreScalesWithSkill();
        testFryingStrategyBonusForHighSkill();
        testGrillingStrategyHighSkillUnlocksBonus();
        testSteamingStrategyScoresHighWithMatchingSkill();
        testRecipeFactoryWiresCorrectStrategy();
        testIngredientFactoryCreatesPerishableWithExpiry();
        testIngredientFactoryNonPerishableNeverExpires();
        testStackedDecoratorsCompoundCorrectly();
        testPremiumDecoratorRaisesSkillRequirement();
        testGlutenFreeDecoratorAddsCorrectSurcharge();
        testObserverPatternNotifiesAllSubscribers();
        testSatisfactionObserverTracksAverage();
        testRecipeServiceRejectsDuplicates();
        testRecipeServiceFiltersAvailableBySkill();
        testInventoryLowStockObserverFires();
        testInventoryServicePurgesExpiredIngredients();
        testPolymorphicBasePricing();
        testGameManagerSeededDataLoadedCorrectly();
        testGameManagerLevelUpCapsAtTen();
        testGameManagerSubmitAndProcessOrder();
        testIngredientConsumeReducesQuantity();
        testIngredientConsumeThrowsWhenUnavailable();
        testIngredientRestockIncreasesQuantity();
        testIngredientRestockThrowsOnNegative();
        testIngredientToString();
        testIngredientSetters();
        testCustomerOrderComplete();
        testCustomerOrderFailedStatus();
        testCustomerOrderGetters();
        testCookingResultGetters();
        testCookingResultStarRatings();
        testBakingStrategyLowSkillFeedback();
        testGrillingStrategyLowSkillFeedback();
        testFryingStrategyLowSkillFeedback();
        testSteamingStrategyLowSkillFeedback();
        testInMemoryRecipeRepositoryUpdate();
        testInMemoryRecipeRepositoryUpdateThrowsOnMissing();
        testInMemoryRecipeRepositoryFindByCategory();
        testInventoryRepositoryFindById();
        testInventoryRepositoryDelete();
        testInventoryRepositoryConsumeThrowsOnMissing();
        testInventoryRepositoryConsumeThrowsOnExpired();
        testInventoryRepositoryFindLowStock();
        testInventoryServiceRestockIngredient();
        testInventoryServiceRestockThrowsOnMissing();
        testInventoryServiceHasAllIngredients();
        testInventoryServiceGetLowStock();
        testOrderServiceHasOrders();
        testOrderServiceGetCompletedOrders();
        testRecipeServiceFindById();
        testRecipeServiceGetByCategory();
        testRecipeServiceRemove();
        testRecipeServiceRemoveThrowsOnMissing();
        testKitchenEventPublisherIgnoresNullAndDuplicates();
        testStockAlertObserverCollectsAndClears();
        testOrderFulfillmentObserverSpend();
        testGameManagerGetters();
        testGameManagerSpendFunds();
        testGameManagerLevelUpAtMaxDoesNothing();
        testExtraSpicyDecoratorAlone();
        testDoubleServingDecoratorAlone();
        testExtraSpicyComplexityCappedAt5();
        System.out.println("\n==========================================");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("==========================================");
        System.exit(failed > 0 ? 1 : 0);
    }
}