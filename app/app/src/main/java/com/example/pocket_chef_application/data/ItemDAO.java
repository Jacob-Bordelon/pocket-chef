package com.example.pocket_chef_application.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDAO {
    @Query("SELECT * FROM DBItem")
    List<DBItem> getAllItems();

    @Insert
    void insertItem(DBItem... items);

    @Query("DELETE FROM DBItem")
    public void nukeTable();
}