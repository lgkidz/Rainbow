package com.odiousPanda.rainbowKt.model.dataSource.aqi


import com.google.gson.annotations.SerializedName

data class Aqi(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
)