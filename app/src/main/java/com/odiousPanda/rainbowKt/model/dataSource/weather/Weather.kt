package com.odiousPanda.rainbowKt.model.dataSource.weather


import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("currently")
    val currently: Currently,
    @SerializedName("daily")
    val daily: Daily,
    @SerializedName("flags")
    val flags: Flags,
    @SerializedName("hourly")
    val hourly: Hourly,
    @SerializedName("latitude")
    val latitude: Float,
    @SerializedName("longitude")
    val longitude: Float,
    @SerializedName("offset")
    val offset: Float,
    @SerializedName("timezone")
    val timezone: String
)