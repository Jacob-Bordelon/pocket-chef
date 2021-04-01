package com.example.pocket_chef_application.Model;

import java.util.ArrayList;
import java.util.List;

public class Food {
    private String name;
    private String image;
    private int fdcId;
    private ArrayList Nutrients;

    public Food() {
    }

    public Food(String name, int fdcId, ArrayList nutrients) {
        this.name = name;
        this.fdcId = fdcId;
        this.Nutrients = nutrients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getFdcId() {
        return fdcId;
    }

    public void setFdcId(int fdcId) {
        this.fdcId = fdcId;
    }

    public ArrayList getNutrients() {
        return Nutrients;
    }

    public void setNutrients(ArrayList nutrients) {
        Nutrients = nutrients;
    }
}
