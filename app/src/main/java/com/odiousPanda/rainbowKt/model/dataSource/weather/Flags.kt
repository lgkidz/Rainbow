package com.odiousPanda.rainbowKt.model.dataSource.weather


import com.google.gson.annotations.SerializedName

data class Flags(
    @SerializedName("meteoalarm-license")
    val meteoalarmLicense: String,
    @SerializedName("nearest-station")
    val nearestStation: Float,
    @SerializedName("sources")
    val sources: List<String>,
    @SerializedName("units")
    val units: String
)