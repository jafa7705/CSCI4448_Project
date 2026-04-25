Recipe Realm — Design Patterns, Architecture & Test Cases
Project: Recipe Realm
Team: Jason Fan, Alec Volkert
Language: Java 
Uses gradle as a build agent
Compile usng gradle compileJava
Run swing mod with gradle run

# Design Patterns

## Strategy Pattern
- Use strategy pattern to implement different cooking classes. Each style of cooking (Frying, Baking, Grilling) all have different algorithms that they need to cook food.
- Interface CookingStrategy is implemented by the three coking subclasses.
- These are called through the recipe factory, which creates the recipe and then uses dependency injection to pass the cooking strategy object into the recipe object.

## Factory Pattern
- The recipe uses a factory pattern to create three main types of recipes (Appetizer, Main Course, Dessert). Currently the factory methods are only called in the test cases. They will be called to build the menu when the game is first started up. Custom order factory is called when a customer places and order and specifies a patience timer.

## Observer Pattern
- KitchenEventPublisher and KitchenObserver is the main observer pattern implmented. Upon completion of an order notifyDishComplete is called. 
- The order fullfillemnt observer recievs the order and the result then calculates the earnings.
- Satisftaction observer calcualtes and adds up the average customer satisfaction.
- A customer observer will also later be implemented. 
