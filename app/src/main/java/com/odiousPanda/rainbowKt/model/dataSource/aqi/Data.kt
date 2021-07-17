package com.odiousPanda.rainbowKt.model.dataSource.aqi


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("aqi")
    val aqi: Float,
    @SerializedName("attributions")
    val attributions: List<Attribution>,
    @SerializedName("city")
    val city: City,
    @SerializedName("debug")
    val debug: Debug,
    @SerializedName("dominentpol")
    val dominentpol: String,
    @SerializedName("iaqi")
    val iaqi: Iaqi,
    @SerializedName("idx")
    val idx: Int,
    @SerializedName("time")
    val time: Time
)