package com.example.pocket_chef_application;

import android.util.Log;

import com.example.pocket_chef_application.data.DBItem;

import java.util.ArrayList;

public class Pantry_Item {
    private String title;
    private DBItem dbItem;
    private boolean isShrink = true;

    public Pantry_Item(DBItem dbItem){
        this.dbItem = dbItem;
        this.title = dbItem.item_Name;

    }

    @Override
    public String toString() {
        return "Pantry_Item{" +
                "title='" + title + '\'';
    }

    public boolean equals(Object o){
        return true;
    }

    public String getTitle() {return title;}
    public void setTitle(String title){this.title = title;}

    public DBItem getDbItem(){return dbItem;}
    public void setDbItem(DBItem item){this.dbItem = item;}

    public boolean isShrink() {
        return isShrink;
    }
    public void setShrink(boolean shrink) {
        isShrink = shrink;
    }
}
