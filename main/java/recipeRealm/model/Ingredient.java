package recipeRealm.model;

import java.time.LocalDate;

public class Ingredient {

    private final String id;
    private final String name;
    private final String unit;       
    private double quantity;      
    private final double requiredAmount;
    private double costPerUnit;
    private LocalDate expiryDate;  

    public Ingredient(String id, String name, String unit, double quantity, double requiredAmount, double costPerUnit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.requiredAmount = requiredAmount;
        this.costPerUnit = costPerUnit;
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    public boolean isAvailable() {
        return !isExpired() && quantity >= requiredAmount;
    }

    public void consume() {
        if (!isAvailable()) throw new IllegalStateException(
                "Cannot consume " + name + ": unavailable or insufficient stock.");
        quantity -= requiredAmount;
    }

    public void restock(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Restock amount must be positive.");
        this.quantity += amount;
    }

    public String getId(){ 
        return id; }

    public String getName(){ 
        return name; 
    }
    public String getUnit(){
         return unit; 
        }
    public double getQuantity(){ 
        return quantity; 
    }
    public double getRequiredAmount() { 
        return requiredAmount; 

    }
    public double getCostPerUnit(){
         return costPerUnit; 
        }

    public LocalDate getExpiryDate(){
         return expiryDate; 
        }

    public void setExpiryDate(LocalDate date) {
         this.expiryDate = date; 
        }

    public void setCostPerUnit(double cost) {
         this.costPerUnit = cost; 
        }

    @Override
    public String toString() {
        return String.format("Ingredient[%s, qty=%.1f%s, req=%.1f, expired=%b]",
                name, quantity, unit, requiredAmount, isExpired());
    }
}