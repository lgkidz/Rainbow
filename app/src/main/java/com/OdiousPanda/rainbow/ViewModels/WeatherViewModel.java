package com.OdiousPanda.rainbow.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.OdiousPanda.rainbow.DataModel.AQI.AirQuality;
import com.OdiousPanda.rainbow.DataModel.Coordinate;
import com.OdiousPanda.rainbow.DataModel.LocationData;
import com.OdiousPanda.rainbow.Repositories.WeatherRepository;

import java.util.List;

public class WeatherViewModel extends AndroidViewModel {
    private static final String TAG = "weatherA";
    private MutableLiveData<List<LocationData>> locationData = new MutableLiveData<>();
    private MutableLiveData<AirQuality> airQualityByCoordinate = new MutableLiveData<>();
    private WeatherRepository repository;
    private LiveData<List<Coordinate>> allSavedCoordinate;

    public WeatherViewModel(Application application) {
        super(application);
        repository = WeatherRepository.getInstance(application);
        Log.d(TAG, "WeatherViewModel: created and calling repo");
        allSavedCoordinate = repository.getAllSavedCoordinates();
    }

    public void insert(Coordinate coordinate) {
        repository.insert(coordinate);
    }

    public void update(Coordinate coordinate) {
        repository.update(coordinate);
    }

    public void delete(Coordinate coordinate) {
        repository.delete(coordinate);
    }

    public void fetchWeather() {
        locationData = repository.getLocationWeathers();
    }

    public void fetchAirQualityByCoordinate(Coordinate coordinate){
        airQualityByCoordinate = repository.getAirQualityByCoordinate(coordinate);
    }

    public LiveData<List<Coordinate>> getAllSavedCoordinate() {
        return allSavedCoordinate;
    }

    public LiveData<List<LocationData>> getLocationData(){
        return locationData;
    }

    public LiveData<AirQuality> getAirQualityByCoordinate(){
        return airQualityByCoordinate;
    }
}
