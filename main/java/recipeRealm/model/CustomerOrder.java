package recipeRealm.model;

import java.time.Instant;
import java.util.UUID;


public class CustomerOrder {

    public enum Status { PENDING, IN_PROGRESS, COMPLETED, FAILED, EXPIRED }

    private final String orderId;
    private final String customerId;
    private final Recipe requestedRecipe;
    private final Instant createdAt;
    private final int patienceSeconds;   
    private Status status;
    private int satisfactionScore;    

    public CustomerOrder(String customerId, Recipe requestedRecipe, int patienceSeconds) {
        this.orderId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.requestedRecipe = requestedRecipe;
        this.createdAt = Instant.now();
        this.patienceSeconds = patienceSeconds;
        this.status = Status.PENDING;
    }

    public boolean isExpired() {
        long elapsed = Instant.now().getEpochSecond() - createdAt.getEpochSecond();
        return elapsed > patienceSeconds;
    }

    public void complete(CookingResult result) {
        if (isExpired()) {
            this.status = Status.EXPIRED;
            this.satisfactionScore = 0;
            return;
        }
        this.status = result.isSuccess() ? Status.COMPLETED : Status.FAILED;
        long elapsed = Instant.now().getEpochSecond() - createdAt.getEpochSecond();
        double timelinessRatio = Math.max(0.0, 1.0 - ((double) elapsed / patienceSeconds));
        this.satisfactionScore = (int) (result.getScore() * 0.7 + timelinessRatio * 30);
    }

    public String getOrderId()            { return orderId; }
    public String getCustomerId()         { return customerId; }
    public Recipe getRequestedRecipe()    { return requestedRecipe; }
    public Instant getCreatedAt()         { return createdAt; }
    public int getPatienceSeconds()       { return patienceSeconds; }
    public Status getStatus()             { return status; }
    public int getSatisfactionScore()     { return satisfactionScore; }
    public void setStatus(Status status)  { this.status = status; }
}
