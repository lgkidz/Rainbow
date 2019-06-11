package com.OdiousPanda.thefweather.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.OdiousPanda.thefweather.Model.SavedCoordinate;
import com.OdiousPanda.thefweather.Model.Weather.Weather;
import com.OdiousPanda.thefweather.Repositories.WeatherRepository;
import java.util.List;

public class WeatherViewModel extends AndroidViewModel {
    private static final String TAG = "weatherA";
    private MutableLiveData<List<Weather>> weatherData = new MutableLiveData<>();
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

    public void fetchWeather(){
        weatherData = repository.getWeather();
    }

    public LiveData<List<SavedCoordinate>> getAllSavedCoordinate(){
        return allSavedCoordinate;
    }


    public LiveData<List<Weather>> getWeatherData(){
        return weatherData;
    }
}
