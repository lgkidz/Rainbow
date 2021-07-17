package com.odiousPanda.rainbowKt.model.dataSource.aqi


import com.google.gson.annotations.SerializedName

data class Debug(
    @SerializedName("sync")
    val sync: String
)