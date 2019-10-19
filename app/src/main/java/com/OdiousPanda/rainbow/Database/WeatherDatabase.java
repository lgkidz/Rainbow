package com.OdiousPanda.rainbow.Database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.OdiousPanda.rainbow.DAOs.CoordinateDAO;
import com.OdiousPanda.rainbow.DataModel.Coordinate;

@Database(entities = {Coordinate.class}, version = 1, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    private static final String TAG = "weatherA";
    private static WeatherDatabase instance;

    public static synchronized WeatherDatabase getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG, "getInstance: new Database instance");
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    WeatherDatabase.class, "rainbowDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract CoordinateDAO savedCoordinateDAO();
}
