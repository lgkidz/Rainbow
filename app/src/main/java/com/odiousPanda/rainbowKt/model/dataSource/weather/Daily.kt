package com.odiousPanda.rainbowKt.model.dataSource.weather


import com.google.gson.annotations.SerializedName

data class Daily(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("summary")
    val summary: String
)