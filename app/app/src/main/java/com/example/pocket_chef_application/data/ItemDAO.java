package com.example.pocket_chef_application.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pocket_chef_application.Model.Food;

import java.util.List;

@Dao
public interface ItemDAO {
    @Query("SELECT * FROM PantryItems")
    List<DBItem> getAllItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DBItem... items);


    @Query("DELETE FROM PantryItems")
    public void nukeTable();

    @Delete
    public void delete(DBItem item);

    @Query("SELECT COUNT(item_name) FROM PantryItems")
    int getCount();





}