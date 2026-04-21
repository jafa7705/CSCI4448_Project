package recipeRealm.model;


public class CookingResult {

    private final boolean success;
    private final int score;          
    private final int timeTakenSeconds;
    private final String cookingMethod;
    private final String feedbackMessage;

    public CookingResult(boolean success, int score, int timeTakenSeconds,
                         String cookingMethod, String feedbackMessage) {
        this.success = success;
        this.score = score;
        this.timeTakenSeconds = timeTakenSeconds;
        this.cookingMethod = cookingMethod;
        this.feedbackMessage = feedbackMessage;
    }

    public boolean isSuccess()           { return success; }
    public int getScore()                { return score; }
    public int getTimeTakenSeconds()     { return timeTakenSeconds; }
    public String getCookingMethod()     { return cookingMethod; }
    public String getFeedbackMessage()   { return feedbackMessage; }

    public int getStarRating() {
        if (score >= 90) return 5;
        if (score >= 75) return 4;
        if (score >= 60) return 3;
        if (score >= 45) return 2;
        return 1;
    }

    @Override
    public String toString() {
        return String.format("CookingResult[method=%s, success=%b, score=%d, stars=%d, time=%ds, feedback='%s']",
                cookingMethod, success, score, getStarRating(), timeTakenSeconds, feedbackMessage);
    }
}