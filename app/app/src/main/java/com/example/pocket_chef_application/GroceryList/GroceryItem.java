package com.example.pocket_chef_application.GroceryList;

import com.example.pocket_chef_application.data.GLItem;

import java.util.UUID;

public class GroceryItem {
    private String name;
    private int amount;
    private GLItem item;

    public GroceryItem(String name, int amount) {
        this.name = name;
        this.amount = amount;
        this.item = new GLItem(name, amount);
    }

    public GroceryItem(GLItem item) {
        this.item = item;
        this.name = item.item_Name;
        this.amount = item.amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if(item.item_Name != name){
            this.item.item_Name = name;
        }
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        if(item.amount != amount){
            this.item.amount = amount;
        }
    }

    public GLItem getItem() {
        return item;
    }

    public void setItem(GLItem item) {
        this.item = item;
    }


}
