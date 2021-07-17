package com.odiousPanda.rainbowKt.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.odiousPanda.rainbowKt.model.dataSource.Coordinate
import com.odiousPanda.rainbowKt.repositories.WeatherRepos
import com.odiousPanda.rainbowKt.apis.Result
import com.odiousPanda.rainbowKt.model.dataSource.LocationData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = WeatherRepos(application)

    val currentTempUnit = repo.currentTempUnit
    val currentDistanceUnit = repo.currentDistanceUnit
    val currentSpeedUnit = repo.currentSpeedUnit
    val currentPressureUnit = repo.currentPressureUnit
    val isExplicit = repo.isExplicit
    val currentBackgroundSetting = repo.currentBackgroundSetting

    var currentLocation: MutableLiveData<Coordinate> = MutableLiveData()

    private val _currentLocationWeather = MutableLiveData<Result<LocationData>>()
    private val _currentWeatherList = MutableLiveData<List<LocationData>>(listOf())

    val currentLocationWeather: LiveData<Result<LocationData>>
        get() = _currentLocationWeather

    val currentWeatherList: LiveData<List<LocationData>>
        get() = _currentWeatherList

    val savedCoordinates = repo.allSavedCoordinates

    private val _selectedLocationData = MutableLiveData<LocationData>()
    private var selectLocationDataPosition = 0

    val selectedLocationData: LiveData<LocationData>
        get() = _selectedLocationData

    fun getWeather(): LocationData? {
        return selectedLocationData.value
    }

    fun updateTempUnit(value: String) {
        repo.updateTempUnit(value)
    }

    fun updateDistanceUnit(value: String) {
        repo.updateDistanceUnit(value)
    }

    fun updateSpeedUnit(value: String) {
        repo.updateSpeedUnit(value)
    }

    fun updatePressureUnit(value: String) {
        repo.updatePressureUnit(value)
    }

    fun updateExplicitSetting(value: Boolean) {
        repo.updateExplicitSetting(value)
    }

    fun updateBackgroundSetting(value: String) {
        repo.updateBackgroundSetting(value)
    }

    fun updateCurrentLocation(coordinate: Coordinate) {
        currentLocation.value = coordinate
        repo.updateCurrentLocation(coordinate)
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    fun getCurrentLocationWeatherAndAQI() {
        viewModelScope.launch {
            repo.getCurrentLocationWeather()
                .onStart {
                    emit(Result.Loading)
                }.collect {
                    _currentLocationWeather.value = it
                }
        }
    }

    fun getWeatherList(){
        viewModelScope.launch {
            val tempList = mutableListOf<LocationData>()
            repo.getWeatherList().toList(tempList)
            _currentWeatherList.postValue(tempList)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val coordinate = _selectedLocationData.value?.coordinate!!
            repo.getWeatherAtCoordinate(coordinate).collect {
                _selectedLocationData.postValue(it)
            }
        }
    }

    fun selectLocation(position: Int) {
        Log.d("weatherA", "viewModel select pos: $position")
        if(position == 0) {
            _selectedLocationData.postValue((_currentLocationWeather.value as Result.Success).data)
        } else {
            _selectedLocationData.postValue(_currentWeatherList.value?.get(position - 1))
        }
        selectLocationDataPosition = position
    }

    fun deleteLocation(position: Int) {
        val listPosition = position - 1
        val coordinate = currentWeatherList.value?.get(listPosition)?.coordinate
        coordinate?.let {
            deleteCoordinate(it)
        }

        if(position <= selectLocationDataPosition) {
            selectLocation(selectLocationDataPosition - 1)
        }
    }

    fun insertCoordinate(coordinate: Coordinate){
        repo.insertCoordinate(coordinate)
    }

    private fun updateCoordinate(coordinate: Coordinate){
        repo.updateCoordinate(coordinate)
    }

    private fun deleteCoordinate(coordinate: Coordinate){
        repo.deleteCoordinate(coordinate)
    }

}