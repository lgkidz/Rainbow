package com.odiousPanda.rainbowKt.model.dataSource

import com.odiousPanda.rainbowKt.model.dataSource.aqi.Aqi
import com.odiousPanda.rainbowKt.model.dataSource.weather.Weather

class LocationData(var coordinate: Coordinate = Coordinate()) {
    var weather: Weather? = null
    var airQuality: Aqi? = null

    fun matchIdWith(obj: LocationData): Boolean {
        return coordinate.id == obj.coordinate.id
    }

}