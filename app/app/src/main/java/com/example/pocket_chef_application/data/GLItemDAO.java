package com.example.pocket_chef_application.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GLItemDAO {
    @Query("SELECT * FROM GroceryItems")
    List<GLItem> getAllGLItems();

    @Insert
    void insertItem(GLItem... items);

    @Delete
    public void deleteItem(GLItem item);

    @Query("SELECT COUNT(item_name) FROM GroceryItems")
    int getCount();

    @Update(entity = GLItem.class)
    public void updateItem(GLItem item);
}
