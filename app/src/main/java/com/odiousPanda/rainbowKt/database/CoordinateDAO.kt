package com.odiousPanda.rainbowKt.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.odiousPanda.rainbowKt.model.dataSource.Coordinate

@Dao
interface CoordinateDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(coordinate: Coordinate)

    @Update
    fun update(coordinate: Coordinate)

    @Delete
    fun delete(coordinate: Coordinate)

    @Query("Select * from location_table")
    fun selectAll(): LiveData<List<Coordinate>>
}
