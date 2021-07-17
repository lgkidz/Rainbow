package com.odiousPanda.rainbowKt.model.dataSource.aqi


import com.google.gson.annotations.SerializedName

data class Time(
    @SerializedName("s")
    val s: String,
    @SerializedName("tz")
    val tz: String,
    @SerializedName("v")
    val v: Long
)