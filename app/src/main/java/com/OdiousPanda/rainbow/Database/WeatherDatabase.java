package com.OdiousPanda.rainbow.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.OdiousPanda.rainbow.DAOs.CoordinateDAO;
import com.OdiousPanda.rainbow.DataModel.Coordinate;

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
                    WeatherDatabase.class, "rainbowDatabase")
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
            //random coordinates for testing purposes
//            coordinateDAO.insert(new Coordinate("41.54366", "36.62115"));
//            coordinateDAO.insert(new Coordinate("78.19304", "-53.67113"));
//            coordinateDAO.insert(new Coordinate("-13.67774","-51.79446"));
//            coordinateDAO.insert(new Coordinate("-11.60935","32.77713"));
//            coordinateDAO.insert(new Coordinate("48.73272","-108.51911"));
            return null;
        }
    }
}
