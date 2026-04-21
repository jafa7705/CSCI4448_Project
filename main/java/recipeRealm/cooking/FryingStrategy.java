package recipeRealm.cooking;

import recipeRealm.model.Recipe;
import recipeRealm.model.CookingResult;


public class FryingStrategy implements CookingStrategy {

    private static final int BASE_TIME_SECONDS = 45;
    private static final double OIL_TEMP_VARIANCE = 0.08; // per skill gap point

    @Override
    public CookingResult cook(Recipe recipe, int skillLevel) {
        int requiredSkill = recipe.getRequiredSkillLevel();
        int skillGap = Math.max(0, requiredSkill - skillLevel);

        double oilControl = Math.max(0.0, 1.0 - (skillGap * OIL_TEMP_VARIANCE));
        double skillBonus = skillLevel >= requiredSkill ? 0.1 : 0.0;
        double rawScore = Math.min(1.0, oilControl + skillBonus);

        int score = (int) (rawScore * 100);
        int timeTaken = BASE_TIME_SECONDS + (recipe.getComplexity() * 5);
        boolean success = score >= 50;

        String feedback = buildFeedback(score);
        return new CookingResult(success, score, timeTaken, getMethodName(), feedback);
    }

    private String buildFeedback(int score) {
        if (score >= 90) return "Crispy perfection — the oil temp was spot on!";
        if (score >= 70) return "Nicely fried, slightly greasy but tasty.";
        if (score >= 50) return "A bit soggy — the oil wasn't hot enough.";
        return "Oil splattered everywhere. The dish is ruined!";
    }

    @Override
    public String getMethodName() { return "Frying"; }

    @Override
    public int getBaseTimeSeconds() { return BASE_TIME_SECONDS; }
}