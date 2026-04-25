package recipeRealm.service;

import recipeRealm.model.Ingredient;
import recipeRealm.model.Recipe;
import recipeRealm.repository.InMemoryRecipeRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


public class RecipeService {

    private final InMemoryRecipeRepository recipeRepository;

    public RecipeService(InMemoryRecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public void addRecipe(Recipe recipe) {
        boolean duplicate = recipeRepository.findAll().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(recipe.getName()));
        if (duplicate) throw new IllegalArgumentException("Recipe already exists: " + recipe.getName());
        recipeRepository.save(recipe);
    }

    public Optional<Recipe> findById(String id) {
        return recipeRepository.findById(id);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public List<Recipe> getAvailableRecipes(int playerSkillLevel) {
        return recipeRepository.findByMaxSkillLevel(playerSkillLevel);
    }

    public List<Recipe> getByCategory(String category) {
        return recipeRepository.findByCategory(category);
    }

    public void removeRecipe(String id) {
        recipeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found: " + id));
        recipeRepository.delete(id);
    }

    public int getTotalRecipeCount() {
        return recipeRepository.size();
    }

    public void removeRecipeWithIngredient(Ingredient ingredient){
        List<Recipe> recipes= getAllRecipes();
        for(Recipe recipe: recipes){
            if(recipe.containsIngredient(ingredient.getId())){
                recipeRepository.delete(recipe.getId());
            }
        }
    }
}