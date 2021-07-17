package com.odiousPanda.rainbowKt.model.dataSource.aqi


import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("geo")
    val geo: List<Double>,
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)