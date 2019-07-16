package com.OdiousPanda.thefweather.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.OdiousPanda.thefweather.DAOs.SavedCoordinateDAO;
import com.OdiousPanda.thefweather.DataModel.SavedCoordinate;

@Database(entities = {SavedCoordinate.class}, version = 1, exportSchema = false)
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

    public abstract SavedCoordinateDAO savedCoordinateDAO();

    private static class InitDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private SavedCoordinateDAO savedCoordinateDAO;

        private InitDbAsyncTask(WeatherDatabase db) {
            savedCoordinateDAO = db.savedCoordinateDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: inserting default location");
            savedCoordinateDAO.insert(new SavedCoordinate("0", "0"));
            savedCoordinateDAO.insert(new SavedCoordinate("64.67895", "108.84549"));
            savedCoordinateDAO.insert(new SavedCoordinate("78.19304", "-53.67113"));
            savedCoordinateDAO.insert(new SavedCoordinate(" -13.67774", "-51.79446"));
            return null;
        }
    }
}
