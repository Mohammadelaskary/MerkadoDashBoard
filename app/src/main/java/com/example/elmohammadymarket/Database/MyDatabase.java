package com.example.elmohammadymarket.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DepartmentNames.class, Product.class}, version = 8, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract MyDao myDao();

    public static MyDatabase myDatabaseInstance;

    public static MyDatabase getInstance(final Context context) {
        if (myDatabaseInstance == null) {
            myDatabaseInstance = Room.databaseBuilder(context,
                    MyDatabase.class, "Mydatabase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return myDatabaseInstance;
    }

}

