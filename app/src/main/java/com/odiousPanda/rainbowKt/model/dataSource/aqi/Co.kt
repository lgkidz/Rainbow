package com.odiousPanda.rainbowKt.model.dataSource.aqi


import com.google.gson.annotations.SerializedName

data class Co(
    @SerializedName("v")
    val v: Float
)