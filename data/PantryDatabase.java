package com.example.pocket_chef_application.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

public class PantryDatabase {

    @Database(entities = {Pantry.class}, version = 1, exportSchema = false)
    public abstract class AppDatabase extends RoomDatabase {
        public abstract pantryDao pantryDao();
    }



}




