package com.odiousPanda.rainbowKt.model.dataSource.aqi


import com.google.gson.annotations.SerializedName

data class So2(
    @SerializedName("v")
    val v: Float
)