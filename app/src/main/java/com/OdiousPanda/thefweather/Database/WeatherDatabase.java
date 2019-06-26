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
import com.OdiousPanda.thefweather.Model.SavedCoordinate;

@Database(entities = {SavedCoordinate.class}, version = 1,exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    private static final String TAG = "weatherA";
    private static WeatherDatabase instance;

    public abstract SavedCoordinateDAO savedCoordinateDAO();

    public static synchronized WeatherDatabase getInstance(Context context){
        if(instance == null){
            Log.d(TAG, "getInstance: new Database instance");
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    WeatherDatabase.class,"weather_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(initCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback initCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new InitDbAsyncTask(instance).execute();
        }
    };


    private static class InitDbAsyncTask extends AsyncTask<Void,Void,Void>{
        private SavedCoordinateDAO savedCoordinateDAO;
        private InitDbAsyncTask(WeatherDatabase db){
            savedCoordinateDAO = db.savedCoordinateDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: inserting default location");
            savedCoordinateDAO.insert(new SavedCoordinate("0","0",null));
            return null;
        }
    }
}
