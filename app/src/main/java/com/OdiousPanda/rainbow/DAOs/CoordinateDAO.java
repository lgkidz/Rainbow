package com.OdiousPanda.rainbow.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.OdiousPanda.rainbow.DataModel.Coordinate;

import java.util.List;

@Dao
public interface CoordinateDAO {
    @Insert
    void insert(Coordinate coordinate);

    @Update
    void update(Coordinate coordinate);

    @Delete
    void delete(Coordinate coordinate);

    @Query("Select * from location_table")
    LiveData<List<Coordinate>> selectAll();
}
