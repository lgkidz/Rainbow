package com.odiousPanda.rainbowKt.model.dataSource.aqi


import com.google.gson.annotations.SerializedName

data class Iaqi(
    @SerializedName("co")
    val co: Co,
    @SerializedName("dew")
    val dew: Dew,
    @SerializedName("h")
    val h: H,
    @SerializedName("no2")
    val no2: No2,
    @SerializedName("o3")
    val o3: O3,
    @SerializedName("p")
    val p: P,
    @SerializedName("pm25")
    val pm25: Pm25,
    @SerializedName("so2")
    val so2: So2,
    @SerializedName("t")
    val t: T,
    @SerializedName("w")
    val w: W
)