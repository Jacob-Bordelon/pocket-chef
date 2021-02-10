package com.example.pocket_chef_application.data;

import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface pantryDao {

    @Query("SELECT * FROM pantry ORDER BY ingID ASC")
    List<Pantry> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Pantry... ingID);

}
