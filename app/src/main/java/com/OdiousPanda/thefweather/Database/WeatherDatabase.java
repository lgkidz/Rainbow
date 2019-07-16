package com.OdiousPanda.thefweather.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.OdiousPanda.thefweather.DAOs.CoordinateDAO;
import com.OdiousPanda.thefweather.DataModel.Coordinate;

@Database(entities = {Coordinate.class}, version = 1, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    private static final String TAG = "weatherA";
    private static WeatherDatabase instance;
    private static RoomDatabase.Callback initCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new InitDbAsyncTask(instance).execute();
        }
    };

    public static synchronized WeatherDatabase getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG, "getInstance: new Database instance");
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    WeatherDatabase.class, "rainbow_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(initCallback)
                    .build();
        }
        return instance;
    }

    public abstract CoordinateDAO savedCoordinateDAO();

    private static class InitDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private CoordinateDAO coordinateDAO;

        private InitDbAsyncTask(WeatherDatabase db) {
            coordinateDAO = db.savedCoordinateDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: inserting default location");
            coordinateDAO.insert(new Coordinate("0", "0"));
            coordinateDAO.insert(new Coordinate("64.67895", "108.84549"));
            coordinateDAO.insert(new Coordinate("78.19304", "-53.67113"));
            coordinateDAO.insert(new Coordinate(" -13.67774", "-51.79446"));
            return null;
        }
    }
}
