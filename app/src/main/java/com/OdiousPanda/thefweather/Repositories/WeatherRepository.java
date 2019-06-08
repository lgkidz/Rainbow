package com.OdiousPanda.thefweather.Repositories;

import androidx.lifecycle.MutableLiveData;

import com.OdiousPanda.thefweather.API.CurrentWeatherCall;
import com.OdiousPanda.thefweather.API.ForecaseWeatherCall;
import com.OdiousPanda.thefweather.API.RetrofitService;
import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;
import com.OdiousPanda.thefweather.Model.Forecast.ForecastWeather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private static WeatherRepository instance;

    public static synchronized WeatherRepository getInstance(){
        if(instance == null){
            instance = new WeatherRepository();
        }
        return instance;
    }

    private CurrentWeatherCall currentWeatherCall;
    private ForecaseWeatherCall forecastWeatherCall;

    private CurrentWeather currentWeather;
    private ForecastWeather forecastWeather;

    public WeatherRepository(){
        currentWeatherCall = RetrofitService.createCurrentWeatherCall();
        forecastWeatherCall = RetrofitService.createForecastWeatherCall();
    }

    public CurrentWeather getCurrentWeatherByCoordinate(String lat, String lon){
        currentWeatherCall.getCurrentWeatherDataByCoordinate(lat, lon).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if(response.isSuccessful()){
                    currentWeather = response.body();
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                currentWeather = null;
            }
        });

        return currentWeather;
    }

    public CurrentWeather getCurrentWeatherByCityName(String cityName){
        currentWeatherCall.getCurrentWeatherDataByCityName(cityName).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if(response.isSuccessful()){
                    currentWeather = response.body();
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                currentWeather = null;
            }
        });

        return currentWeather;
    }

    public ForecastWeather getForecastWeatherByCoordinate(String lat, String lon){
               forecastWeatherCall.getForecastWeatherByCityCoordinate(lat, lon).enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                if(response.isSuccessful()){
                    forecastWeather = response.body();
                }
            }

            @Override
            public void onFailure(Call<ForecastWeather> call, Throwable t) {
                forecastWeather = null;
            }
        });

        return forecastWeather;
    }

    public ForecastWeather getForecastWeatherByCityName(String cityName){
        forecastWeatherCall.getForecastWeatherByCityName(cityName).enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                if (response.isSuccessful()){
                    forecastWeather = response.body();
                }
            }

            @Override
            public void onFailure(Call<ForecastWeather> call, Throwable t) {
                forecastWeather = null;
            }
        });

        return forecastWeather;
    }
}
