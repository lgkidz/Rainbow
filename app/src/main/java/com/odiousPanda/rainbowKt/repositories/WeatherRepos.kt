package com.odiousPanda.rainbowKt.repositories

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.odiousPanda.rainbowKt.apis.Result
import com.odiousPanda.rainbowKt.apis.RetrofitService
import com.odiousPanda.rainbowKt.database.CoordinateDAO
import com.odiousPanda.rainbowKt.database.WeatherDatabase
import com.odiousPanda.rainbowKt.model.dataSource.Coordinate
import com.odiousPanda.rainbowKt.model.dataSource.LocationData
import com.odiousPanda.rainbowKt.utilities.PreferencesUtil
import com.odiousPanda.rainbowKt.widgets.NormalWidget
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class WeatherRepos(private var context: Context) {
    companion object {
        const val TAG = "weatherA"
    }

    private val coordinateDAO: CoordinateDAO = WeatherDatabase.getInstance(context)!!.savedCoordinateDAO()
    val allSavedCoordinates: LiveData<List<Coordinate>> = coordinateDAO.selectAll()
    private var locale: String

    init {
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].language
        } else {
            context.resources.configuration.locale.language
        }
        if (locale != "vi") {
            locale = "en"
        }
    }

    val currentTempUnit = MutableLiveData(PreferencesUtil.getTemperatureUnit(context).toString())
    val currentDistanceUnit = MutableLiveData(PreferencesUtil.getDistanceUnit(context).toString())
    val currentSpeedUnit = MutableLiveData(PreferencesUtil.getSpeedUnit(context).toString())
    val currentPressureUnit = MutableLiveData(PreferencesUtil.getPressureUnit(context).toString())
    val isExplicit = MutableLiveData(PreferencesUtil.isExplicit(context))
    val currentBackgroundSetting =
        MutableLiveData(PreferencesUtil.getBackgroundSetting(context).toString())

    private val weatherCall = RetrofitService.createWeatherCall()
    private val aqiCall = RetrofitService.createAQICall()



    fun updateTempUnit(value: String) {
        PreferencesUtil.setUnitSetting(context, PreferencesUtil.TEMPERATURE_UNIT, value)
        currentTempUnit.value = value

        val updateWidgetIntent = Intent(NormalWidget.ACTION_UPDATE)
        context.sendBroadcast(updateWidgetIntent)
    }

    fun updateDistanceUnit(value: String) {
        PreferencesUtil.setUnitSetting(context, PreferencesUtil.DISTANCE_UNIT, value)
        currentDistanceUnit.value = value
    }

    fun updateSpeedUnit(value: String) {
        PreferencesUtil.setUnitSetting(context, PreferencesUtil.SPEED_UNIT, value)
        currentSpeedUnit.value = value
    }

    fun updatePressureUnit(value: String) {
        PreferencesUtil.setUnitSetting(context, PreferencesUtil.PRESSURE_UNIT, value)
        currentPressureUnit.value = value
    }

    fun updateExplicitSetting(value: Boolean) {
        PreferencesUtil.setExplicitSetting(context, value)
        isExplicit.value = value

        val updateWidgetIntent = Intent(NormalWidget.ACTION_UPDATE)
        context.sendBroadcast(updateWidgetIntent)
    }

    fun updateBackgroundSetting(value: String) {
        PreferencesUtil.setBackgroundSetting(context, value)
        currentBackgroundSetting.value = value
    }

    private fun getCurrentLocation(): Coordinate {
        return PreferencesUtil.getCurrentLocation(context)
    }

    fun updateCurrentLocation(coordinate: Coordinate) {
        PreferencesUtil.setCurrentLocation(context, coordinate)
    }

    fun insertCoordinate(coordinate: Coordinate) {
        CoroutineScope(Dispatchers.IO).launch {
            coordinateDAO.insert(coordinate)
        }
    }

    fun updateCoordinate(coordinate: Coordinate) {
        CoroutineScope(Dispatchers.IO).launch {
            coordinateDAO.update(coordinate)
        }
    }

    fun deleteCoordinate(coordinate: Coordinate) {
        CoroutineScope(Dispatchers.IO).launch {
            coordinateDAO.delete(coordinate)
        }
    }

    suspend fun getCurrentLocationWeather(): Flow<Result<LocationData>> = flow {
        val currentLocation = getCurrentLocation()

        val locationData = LocationData(currentLocation)

        val weatherResponse = withContext(Dispatchers.IO) {
            weatherCall.getWeather(currentLocation.lat, currentLocation.lon, locale)
        }

        val aqiResponse = withContext(Dispatchers.IO) {
            aqiCall.getAirQuality(currentLocation.lat, currentLocation.lon)
        }

        if (weatherResponse.isSuccessful && aqiResponse.isSuccessful) {
            locationData.weather = weatherResponse.body()
            locationData.airQuality = aqiResponse.body()
            emit(Result.Success(locationData))
        } else {
            emit(Result.Error)
        }
    }

    suspend fun getWeatherAtCoordinate(coordinate: Coordinate): Flow<LocationData> = flow {
        val locationData = LocationData(coordinate)
        val weatherResponse = withContext(Dispatchers.IO) {
            weatherCall.getWeather(coordinate.lat, coordinate.lon, locale)
        }
        val aqiResponse = withContext(Dispatchers.IO) {
            aqiCall.getAirQuality(coordinate.lat, coordinate.lon)
        }

        if(weatherResponse.isSuccessful) locationData.weather = weatherResponse.body()
        if(aqiResponse.isSuccessful) locationData.airQuality = aqiResponse.body()

        emit(locationData)
    }

    suspend fun getWeatherList(): Flow<LocationData> = flow {
            allSavedCoordinates.value?.let { list ->
            list.forEach {
                val locationData = LocationData(it)
                val weatherResponse = withContext(Dispatchers.IO) {
                    weatherCall.getWeather(it.lat, it.lon, locale)
                }
                val aqiResponse = withContext(Dispatchers.IO) {
                    aqiCall.getAirQuality(it.lat, it.lon)
                }

                if(weatherResponse.isSuccessful) locationData.weather = weatherResponse.body()
                if(aqiResponse.isSuccessful) locationData.airQuality = aqiResponse.body()

                emit(locationData)
            }
        }
    }
}