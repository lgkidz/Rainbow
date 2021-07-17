package com.odiousPanda.rainbowKt.model.dataSource.weather


import com.google.gson.annotations.SerializedName

data class Currently(
    @SerializedName("apparentTemperature")
    val apparentTemperature: Float,
    @SerializedName("cloudCover")
    val cloudCover: Float,
    @SerializedName("dewPoint")
    val dewPoint: Float,
    @SerializedName("humidity")
    val humidity: Float,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("ozone")
    val ozone: Float,
    @SerializedName("precipIntensity")
    val precipIntensity: Float,
    @SerializedName("precipProbability")
    val precipProbability: Float,
    @SerializedName("precipType")
    val precipType: String,
    @SerializedName("pressure")
    val pressure: Float,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("temperature")
    val temperature: Float,
    @SerializedName("time")
    val time: Long,
    @SerializedName("uvIndex")
    val uvIndex: Float,
    @SerializedName("visibility")
    val visibility: Float,
    @SerializedName("windBearing")
    val windBearing: Float,
    @SerializedName("windGust")
    val windGust: Float,
    @SerializedName("windSpeed")
    val windSpeed: Float
)