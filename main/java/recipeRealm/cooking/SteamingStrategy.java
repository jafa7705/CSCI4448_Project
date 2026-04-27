package recipeRealm.cooking;

import recipeRealm.model.CookingResult;
import recipeRealm.model.Recipe;

public class SteamingStrategy implements CookingStrategy {

    private static final int BASE_TIME_SECONDS = 50;
    private static final double BLANDNESS_PENALTY_PER_SKILL_GAP = 0.04;
    private static final int PRECISION_BONUS = 10; // awarded when skill >= required

    @Override
    public CookingResult cook(Recipe recipe, int skillLevel) {
        int requiredSkill = recipe.getRequiredSkillLevel();
        int skillGap = Math.max(0, requiredSkill - skillLevel);

        double baseScore = 65.0 - (skillGap * BLANDNESS_PENALTY_PER_SKILL_GAP * 100);
        baseScore = Math.max(0, Math.min(85, baseScore));

        int precisionBonus = skillLevel >= requiredSkill ? PRECISION_BONUS : 0;
        int score = (int) Math.min(100, baseScore + precisionBonus);

        int timeTaken = BASE_TIME_SECONDS + (recipe.getComplexity() * 8);
        boolean success = score >= 50;

        String feedback = buildFeedback(score, precisionBonus > 0);
        return new CookingResult(success, score, timeTaken, getMethodName(), feedback);
    }

    private String buildFeedback(int score, boolean precisionBonus) {
        if (precisionBonus && score >= 90) return "Perfectly steamed";
        if (score >= 70) return "Well steamed with a pleasant, clean flavour.";
        if (score >= 50) return "Cooked through, though a bit bland and watery.";
        return "Hopelessly waterlogged";
    }

    @Override
    public String getMethodName() { return "Steaming"; }

    @Override
    public int getBaseTimeSeconds() { return BASE_TIME_SECONDS; }
}
