package com.odiousPanda.rainbowKt.model.dataSource

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity(tableName = "location_table")
class Coordinate {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @SerializedName("lon")
    @Expose
    var lon: String = "0"

    @SerializedName("lat")
    @Expose
    var lat: String = "0"

    @SerializedName("name")
    @Expose
    var name: String = ""

    constructor()

    @Ignore
    constructor(lat: String, lon: String) {
        this.lat = lat
        this.lon = lon
    }

}