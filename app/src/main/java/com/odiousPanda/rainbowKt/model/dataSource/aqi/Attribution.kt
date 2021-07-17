package com.odiousPanda.rainbowKt.model.dataSource.aqi


import com.google.gson.annotations.SerializedName

data class Attribution(
    @SerializedName("logo")
    val logo: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)