package recipeRealm.cooking;

import recipeRealm.model.Recipe;
import recipeRealm.model.CookingResult;


public class GrillingStrategy implements CookingStrategy {

    private static final int BASE_TIME_SECONDS = 60;
    private static final int GRILL_MARKS_BONUS = 15; 

    @Override
    public CookingResult cook(Recipe recipe, int skillLevel) {
        int requiredSkill = recipe.getRequiredSkillLevel();
        double baseScore = 50.0 + (skillLevel * 5.0) - (requiredSkill * 3.0);
        baseScore = Math.max(0, Math.min(85, baseScore));

        int grillBonus = skillLevel >= 8 ? GRILL_MARKS_BONUS : 0;
        int score = (int) Math.min(100, baseScore + grillBonus);

        int timeTaken = BASE_TIME_SECONDS + (recipe.getComplexity() * 10);
        boolean success = score >= 50;

        String feedback = buildFeedback(score, grillBonus > 0);
        return new CookingResult(success, score, timeTaken, getMethodName(), feedback);
    }

    private String buildFeedback(int score, boolean hasGrillMarks) {
        if (hasGrillMarks && score >= 90) return "Masterful grill marks — restaurant quality!";
        if (score >= 70) return "Well grilled with a nice char.";
        if (score >= 50) return "Cooked through, but the exterior lacks colour.";
        return "Burnt to charcoal on the outside, raw inside.";
    }

    @Override
    public String getMethodName() { return "Grilling"; }

    @Override
    public int getBaseTimeSeconds() { return BASE_TIME_SECONDS; }
}
