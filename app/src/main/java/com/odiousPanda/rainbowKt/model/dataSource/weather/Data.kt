package com.odiousPanda.rainbowKt.model.dataSource.weather


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("apparentTemperatureHigh")
    val apparentTemperatureHigh: Float,
    @SerializedName("apparentTemperatureHighTime")
    val apparentTemperatureHighTime: Long,
    @SerializedName("apparentTemperatureLow")
    val apparentTemperatureLow: Float,
    @SerializedName("apparentTemperatureLowTime")
    val apparentTemperatureLowTime: Long,
    @SerializedName("apparentTemperatureMax")
    val apparentTemperatureMax: Float,
    @SerializedName("apparentTemperatureMaxTime")
    val apparentTemperatureMaxTime: Long,
    @SerializedName("apparentTemperatureMin")
    val apparentTemperatureMin: Float,
    @SerializedName("apparentTemperatureMinTime")
    val apparentTemperatureMinTime: Long,
    @SerializedName("cloudCover")
    val cloudCover: Float,
    @SerializedName("dewPoint")
    val dewPoint: Float,
    @SerializedName("humidity")
    val humidity: Float,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("moonPhase")
    val moonPhase: Float,
    @SerializedName("ozone")
    val ozone: Float,
    @SerializedName("precipIntensity")
    val precipIntensity: Float,
    @SerializedName("precipIntensityMax")
    val precipIntensityMax: Float,
    @SerializedName("precipIntensityMaxTime")
    val precipIntensityMaxTime: Long,
    @SerializedName("precipProbability")
    val precipProbability: Float,
    @SerializedName("precipType")
    val precipType: String,
    @SerializedName("pressure")
    val pressure: Float,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("sunriseTime")
    val sunriseTime: Long,
    @SerializedName("sunsetTime")
    val sunsetTime: Long,
    @SerializedName("temperatureHigh")
    val temperatureHigh: Float,
    @SerializedName("temperatureHighTime")
    val temperatureHighTime: Long,
    @SerializedName("temperatureLow")
    val temperatureLow: Float,
    @SerializedName("temperatureLowTime")
    val temperatureLowTime: Long,
    @SerializedName("temperatureMax")
    val temperatureMax: Float,
    @SerializedName("temperatureMaxTime")
    val temperatureMaxTime: Long,
    @SerializedName("temperatureMin")
    val temperatureMin: Float,
    @SerializedName("temperatureMinTime")
    val temperatureMinTime: Long,
    @SerializedName("time")
    val time: Long,
    @SerializedName("uvIndex")
    val uvIndex: Float,
    @SerializedName("uvIndexTime")
    val uvIndexTime: Long,
    @SerializedName("visibility")
    val visibility: Float,
    @SerializedName("windBearing")
    val windBearing: Float,
    @SerializedName("windGust")
    val windGust: Float,
    @SerializedName("windGustTime")
    val windGustTime: Long,
    @SerializedName("windSpeed")
    val windSpeed: Float
)