package recipeRealm.model;

import recipeRealm.cooking.CookingStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public abstract class BaseRecipe implements Recipe {

    private final String id;
    private final String name;
    private final String description;
    private final List<Ingredient> ingredients;
    private final CookingStrategy cookingStrategy;
    private final int requiredSkillLevel;
    private final int complexity;
    private final int preparationTimeSeconds;

    protected BaseRecipe(String name, String description,
                         List<Ingredient> ingredients,
                         CookingStrategy cookingStrategy,
                         int requiredSkillLevel, int complexity,
                         int preparationTimeSeconds) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.ingredients = new ArrayList<>(ingredients);
        this.cookingStrategy = cookingStrategy;
        this.requiredSkillLevel = requiredSkillLevel;
        this.complexity = complexity;
        this.preparationTimeSeconds = preparationTimeSeconds;
    }

    @Override public String getId()                       { return id; }
    @Override public String getName()                     { return name; }
    @Override public String getDescription()              { return description; }
    @Override public List<Ingredient> getIngredients()    { return ingredients; }
    @Override public CookingStrategy getCookingStrategy() { return cookingStrategy; }
    @Override public int getRequiredSkillLevel()          { return requiredSkillLevel; }
    @Override public int getComplexity()                  { return complexity; }
    @Override public int getPreparationTimeSeconds()      { return preparationTimeSeconds; }

}
