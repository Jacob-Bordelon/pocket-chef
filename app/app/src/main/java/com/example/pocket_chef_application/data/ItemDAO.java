package com.example.pocket_chef_application.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDAO {
    @Query("SELECT * FROM item")
    List<Item> getAllItems();

    @Insert
    void insertItem(Item... items);

    @Query("DELETE FROM item")
    public void nukeTable();
}