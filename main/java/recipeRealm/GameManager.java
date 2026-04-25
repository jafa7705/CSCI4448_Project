package recipeRealm;

import recipeRealm.factory.CustomerOrderFactory;
import recipeRealm.factory.IngredientFactory;
import recipeRealm.factory.RecipeFactory;
import recipeRealm.model.CustomerOrder;
import recipeRealm.model.CookingResult;
import recipeRealm.model.Ingredient;
import recipeRealm.model.Recipe;
import recipeRealm.observer.KitchenEventPublisher;
import recipeRealm.observer.OrderFulfillmentObserver;
import recipeRealm.observer.SatisfactionObserver;
import recipeRealm.observer.StockAlertObserver;
import recipeRealm.repository.InMemoryRecipeRepository;
import recipeRealm.repository.InventoryRepository;
import recipeRealm.service.InventoryService;
import recipeRealm.service.OrderService;
import recipeRealm.service.RecipeService;

import java.util.List;


public class GameManager {

    

    private final RecipeService recipeService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final OrderFulfillmentObserver fulfillmentObserver;
    private final SatisfactionObserver satisfactionObserver;
    private final StockAlertObserver stockObserver;
    private int playerSkillLevel;


    public GameManager(int startingSkillLevel) {
        this.playerSkillLevel = startingSkillLevel;


        InMemoryRecipeRepository recipeRepo = new InMemoryRecipeRepository();
        InventoryRepository inventoryRepo = new InventoryRepository();


        this.recipeService   = new RecipeService(recipeRepo);
        this.inventoryService = new InventoryService(inventoryRepo);


        KitchenEventPublisher publisher = new KitchenEventPublisher();
        this.fulfillmentObserver  = new OrderFulfillmentObserver();
        this.satisfactionObserver = new SatisfactionObserver();
        this.stockObserver = new StockAlertObserver();
        publisher.registerObserver(fulfillmentObserver);
        publisher.registerObserver(satisfactionObserver);
        inventoryRepo.addObserver(stockObserver);


        this.orderService = new OrderService(publisher, inventoryRepo);
    }






    public void seedDefaultData() {
        seedIngredients();
        seedRecipes();
    }

    private void seedIngredients() {
        List<Ingredient> starters = List.of(
            IngredientFactory.createDryGood("ing-flour",  "Flour",  500, 100, 0.002),
            IngredientFactory.createDryGood("ing-sugar",  "Sugar",  300,  50, 0.003),
            IngredientFactory.createDryGood("ing-salt",   "Salt",   200,  10, 0.001),
            IngredientFactory.createProtein("ing-egg",    "Egg",    "pieces", 12, 2, 0.30, 14),
            IngredientFactory.createDairy(  "ing-butter", "Butter", "g",  250, 50, 0.012, 21),
            IngredientFactory.createDairy(  "ing-milk",   "Milk",   "ml", 1000, 200, 0.002, 7),
            IngredientFactory.createProduce("ing-tomato", "Tomato", 10, 2, 0.50, 5),
            IngredientFactory.createProtein("ing-chicken","Chicken","g",  600, 150, 0.015, 3),
            IngredientFactory.createProduce("ing-lemon",  "Lemon",  6, 1, 0.40, 10),
            IngredientFactory.createNonPerishable("ing-oil", "Olive Oil", "ml", 500, 30, 0.005)
        );
        starters.forEach(inventoryService::addIngredient);
    }

    private void seedRecipes() {
        List<Ingredient> eggFlour = List.of(
            IngredientFactory.createNonPerishable("ing-egg",   "Egg",   "pieces", 12, 2, 0.30),
            IngredientFactory.createNonPerishable("ing-flour", "Flour", "g",     500, 100, 0.002)
        );
        List<Ingredient> chickenLemon = List.of(
            IngredientFactory.createNonPerishable("ing-chicken", "Chicken", "g", 600, 150, 0.015),
            IngredientFactory.createNonPerishable("ing-lemon",   "Lemon",   "pieces", 6, 1, 0.40)
        );
        List<Ingredient> tomatoOil = List.of(
            IngredientFactory.createNonPerishable("ing-tomato", "Tomato",    "pieces", 10, 2, 0.50),
            IngredientFactory.createNonPerishable("ing-oil",    "Olive Oil", "ml",    500, 30, 0.005)
        );
        List<Ingredient> butterSugar = List.of(
            IngredientFactory.createNonPerishable("ing-butter", "Butter", "g",   250,  50, 0.012),
            IngredientFactory.createNonPerishable("ing-sugar",  "Sugar",  "g",   300,  50, 0.003),
            IngredientFactory.createNonPerishable("ing-flour",  "Flour",  "g",   500, 100, 0.002)
        );

        tryAdd(RecipeFactory.createAppetizer("Bruschetta",      "Grilled bread with tomato",        tomatoOil,    "grilling", 1, 1));
        tryAdd(RecipeFactory.createAppetizer("Steamed Dumplings","Delicate pork parcels",           eggFlour,     "steaming", 3, 2));
        tryAdd(RecipeFactory.createMainCourse("Lemon Chicken",  "Zesty pan-seared chicken",         chickenLemon, "frying",   4, 3));
        tryAdd(RecipeFactory.createMainCourse("Grilled Ribeye", "Prime cut with herb butter",       chickenLemon, "grilling", 6, 4));
        tryAdd(RecipeFactory.createMainCourse("Steamed Sea Bass","Delicately flavoured whole fish", chickenLemon, "steaming", 5, 3));
        tryAdd(RecipeFactory.createDessert("Butter Cookies",    "Classic melt-in-mouth shortbread", butterSugar,  "baking",   2, 2));
        tryAdd(RecipeFactory.createDessert("Croissant",         "Laminated buttery pastry",         butterSugar,  "baking",   7, 5));
    }

    private void tryAdd(Recipe recipe) {
        try { recipeService.addRecipe(recipe); }
        catch (IllegalArgumentException ignored) {}
    }


    
    public CustomerOrder submitRandomOrder() {
        List<Recipe> available = recipeService.getAvailableRecipes(playerSkillLevel);
        if (available.isEmpty()) throw new IllegalStateException("No recipes available for skill level " + playerSkillLevel);
        CustomerOrder order = CustomerOrderFactory.createRandom(available);
        orderService.enqueueOrder(order);
        System.out.printf("[Queue] Order added for '%s'. Queue size: %d%n",
                order.getRequestedRecipe().getName(), orderService.getQueueSize());
        return order;
    }


    public CookingResult processNextOrder() {
        return orderService.processNextOrder(playerSkillLevel);
    }


    public void levelUp() {
        if (playerSkillLevel < 10) {
            playerSkillLevel++;
            System.out.println("[Progress] Skill level increased to " + playerSkillLevel);
        }
    }


    public int getPlayerSkillLevel()     { return playerSkillLevel; }
    public double getTotalEarnings()     { return fulfillmentObserver.getTotalEarnings(); }
    public int getAverageSatisfaction()  { return satisfactionObserver.getAverageSatisfaction(); }
    public int getOrderQueueSize()       { return orderService.getQueueSize(); }
    public List<Recipe> getAllRecipes()  { return recipeService.getAllRecipes(); }
    public StockAlertObserver getStockObserver(){
        return stockObserver;
    }

    public RecipeService getRecipeService()       { return recipeService; }
    public InventoryService getInventoryService() { return inventoryService; }
    public OrderService getOrderService()         { return orderService; }
    public void spendFunds(double amount) {
        fulfillmentObserver.spend(amount);
    }
}