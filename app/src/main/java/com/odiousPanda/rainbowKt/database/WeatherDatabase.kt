package com.odiousPanda.rainbowKt.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.odiousPanda.rainbowKt.model.dataSource.Coordinate

@Database(entities = [Coordinate::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun savedCoordinateDAO(): CoordinateDAO

    companion object {
        private const val TAG = "weatherA"
        private const val DB_NAME = "rainbowKtDb"
        private var instance: WeatherDatabase? = null

        @Synchronized
        fun getInstance(context: Context): WeatherDatabase? {
            if (instance == null) {
                Log.d(
                    TAG,
                    "getInstance: new Database instance"
                )
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }
}
