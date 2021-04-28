package com.example.pocket_chef_application.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {DBItem.class, GLItem.class},
        version = 1,
        exportSchema = false
)
public abstract class LocalDB extends RoomDatabase {

    public abstract ItemDAO itemDAO();

    public abstract GLItemDAO GlDAO();

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
