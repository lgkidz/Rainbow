package com.OdiousPanda.thefweather.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;
import com.OdiousPanda.thefweather.Model.Forecast.ForecastWeather;
import com.OdiousPanda.thefweather.Model.SavedCoordinate;
import com.OdiousPanda.thefweather.Repositories.WeatherRepository;

import java.util.ArrayList;
import java.util.List;

public class WeatherViewModel extends ViewModel {
    private MutableLiveData<List<CurrentWeather>> currentWeatherData;
    private MutableLiveData<List<ForecastWeather>> forecastWeatherData;

    private WeatherRepository repository;

    public void fetchCurrentWeather(List<SavedCoordinate> currentWeatherList){
        repository = WeatherRepository.getInstance();
        List<CurrentWeather> tempList = new ArrayList<>();
        for(SavedCoordinate c : currentWeatherList){
            if(c.getName() == null || c.getName().trim().isEmpty()){
                tempList.add(repository.getCurrentWeatherByCoordinate(c.getLat(),c.getLon()));
            }
            else{
                tempList.add(repository.getCurrentWeatherByCityName(c.getName()));
            }
        }
        currentWeatherData.setValue(tempList);
    }

    public void fetchForecastWeather(List<SavedCoordinate> forecastWeatherList){
        repository = WeatherRepository.getInstance();
        List<ForecastWeather> tempList = new ArrayList<>();
        for(SavedCoordinate c : forecastWeatherList){
            if(c.getName() == null || c.getName().trim().isEmpty()){
                tempList.add(repository.getForecastWeatherByCoordinate(c.getLat(),c.getLon()));
            }
            else{
                tempList.add(repository.getForecastWeatherByCityName(c.getName()));
            }
        }
        forecastWeatherData.setValue(tempList);
    }

    public LiveData<List<CurrentWeather>> getCurrentWeatherData(List<SavedCoordinate> currentWeatherList){
        fetchCurrentWeather(currentWeatherList);
        return currentWeatherData;
    }

    public LiveData<List<ForecastWeather>> getForecastWeatherData(List<SavedCoordinate> forecastWeatherList){
        fetchForecastWeather(forecastWeatherList);
        return forecastWeatherData;
    }
}
