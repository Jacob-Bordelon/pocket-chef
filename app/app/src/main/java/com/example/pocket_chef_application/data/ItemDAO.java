package com.example.pocket_chef_application.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDAO {
    @Query("SELECT * FROM DBItem")
    List<DBItem> getAllItems();

    @Query("SELECT * FROM GLItem")
    List<GLItem> getAllGLItems();

    @Insert
    void insertItem(DBItem... items);

    @Insert
    void insertGLItem(GLItem... items);

    @Query("DELETE FROM DBItem")
    public void nukeTable();

    @Delete
    public void delete(DBItem item);

    @Delete
    public void removeGLItem(GLItem item);

    @Query("SELECT COUNT(item_name) FROM DBItem")
    int getCount();

    @Query("SELECT COUNT(item_name) FROM GLItem")
    int getGLCount();

    @Update(entity = GLItem.class)
    public void updateGLItem(GLItem item);



}