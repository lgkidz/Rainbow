package com.odiousPanda.rainbowKt.model.dataSource.aqi


import com.google.gson.annotations.SerializedName

data class O3(
    @SerializedName("v")
    val v: Float
)