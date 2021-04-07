package com.example.pocket_chef_application.Gen_Recipes;

public class Ingredient {
    private int amount;
    private String name, measurement;

    public Ingredient(int amount, String name, String measurement) {
        this.amount = amount;
        this.name = name;
        this.measurement = measurement;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Ingredient() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getFilterName(){
        return this.name.split(",")[0];
    }

}
