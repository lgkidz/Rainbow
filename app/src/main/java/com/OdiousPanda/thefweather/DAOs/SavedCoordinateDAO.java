package com.OdiousPanda.thefweather.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.OdiousPanda.thefweather.Model.SavedCoordinate;

import java.util.List;

@Dao
public interface SavedCoordinateDAO {
    @Insert
    void insert(SavedCoordinate savedCoordinate);

    @Update
    void update(SavedCoordinate savedCoordinate);

    @Delete
    void delete(SavedCoordinate savedCoordinate);

    @Query("Select * from location_table")
    LiveData<List<SavedCoordinate>> selectAll();
}
