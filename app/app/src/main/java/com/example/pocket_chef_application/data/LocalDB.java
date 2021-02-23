package com.example.pocket_chef_application.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Item.class},version = 1, exportSchema = false)
public abstract class LocalDB extends RoomDatabase {

    public abstract ItemDAO itemDAO();

    private static LocalDB INSTANCE;

    public static LocalDB getDBInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LocalDB.class, "iid")
                    .allowMainThreadQueries()
                    .build();

        }
        return INSTANCE;
    }
}
