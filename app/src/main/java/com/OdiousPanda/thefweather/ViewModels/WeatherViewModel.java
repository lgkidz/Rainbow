package com.OdiousPanda.thefweather.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;
import com.OdiousPanda.thefweather.Model.Forecast.ForecastWeather;
import com.OdiousPanda.thefweather.Model.SavedCoordinate;
import com.OdiousPanda.thefweather.Repositories.WeatherRepository;
import java.util.List;

public class WeatherViewModel extends AndroidViewModel {
    private static final String TAG = "weatherA";
    private MutableLiveData<List<CurrentWeather>> currentWeatherData = new MutableLiveData<>();
    private MutableLiveData<List<ForecastWeather>> forecastWeatherData = new MutableLiveData<>();
    private WeatherRepository repository;
    private LiveData<List<SavedCoordinate>> allSavedCoordinate;

    public WeatherViewModel(Application application){
        super(application);
        repository = WeatherRepository.getInstance(application);
        Log.d(TAG, "WeatherViewModel: created and calling repo");
        allSavedCoordinate = repository.getAllSavedCoordinates();
    }

    public void insert(SavedCoordinate savedCoordinate){
        repository.insert(savedCoordinate);
    }

    public void update(SavedCoordinate savedCoordinate){
        repository.update(savedCoordinate);
    }

    public void delete(SavedCoordinate savedCoordinate){
        repository.delete(savedCoordinate);
    }

    public void fetchCurrentWeather(){
        currentWeatherData = repository.getCurrentWeather();
    }

    public void fetchForecastWeather(){
        forecastWeatherData = repository.getForecastWeather();
    }

    public LiveData<List<SavedCoordinate>> getAllSavedCoordinate(){
        return allSavedCoordinate;
    }

    public LiveData<List<CurrentWeather>> getCurrentWeatherData(){
        return currentWeatherData;
    }

    public LiveData<List<ForecastWeather>> getForecastWeatherData(){
        return forecastWeatherData;
    }
}
