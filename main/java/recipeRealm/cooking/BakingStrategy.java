package recipeRealm.cooking;

import recipeRealm.model.Recipe;
import recipeRealm.model.CookingResult;


public class BakingStrategy implements CookingStrategy {

    private static final int BASE_TIME_SECONDS = 120; // 2 minutes in-game
    private static final double BURN_RISK_PER_MISSING_SKILL = 0.05;

    @Override
    public CookingResult cook(Recipe recipe, int skillLevel) {
        int requiredSkill = recipe.getRequiredSkillLevel();
        int skillGap = Math.max(0, requiredSkill - skillLevel);
        double burnRisk = skillGap * BURN_RISK_PER_MISSING_SKILL;

        double temperatureAccuracy = Math.max(0.0, 1.0 - burnRisk);
        int score = (int) (temperatureAccuracy * 100);

        int timeTaken = BASE_TIME_SECONDS + (recipe.getComplexity() * 15);
        boolean success = score >= 50;

        String feedback = buildFeedback(score, skillGap);
        return new CookingResult(success, score, timeTaken, getMethodName(), feedback);
    }

    private String buildFeedback(int score, int skillGap) {
        if (score >= 90) return "Perfectly baked — golden crust, moist inside!";
        if (score >= 70) return "Nicely baked, but the edges are a bit dark.";
        if (score >= 50) return "Edible, though the centre is slightly underdone.";
        if (skillGap > 3) return "Burned! You need more skill before attempting this recipe.";
        return "Catastrophically underbaked — still raw inside.";
    }

    @Override
    public String getMethodName() { return "Baking"; }

    @Override
    public int getBaseTimeSeconds() { return BASE_TIME_SECONDS; }
}