package com.OdiousPanda.thefweather.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.OdiousPanda.thefweather.API.AQICall;
import com.OdiousPanda.thefweather.API.RetrofitService;
import com.OdiousPanda.thefweather.API.WeatherCall;
import com.OdiousPanda.thefweather.DAOs.SavedCoordinateDAO;
import com.OdiousPanda.thefweather.Database.WeatherDatabase;
import com.OdiousPanda.thefweather.Model.AQI.AirQuality;
import com.OdiousPanda.thefweather.Model.SavedCoordinate;
import com.OdiousPanda.thefweather.Model.Weather.Weather;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private static final String TAG = "weatherA";

    private static WeatherRepository instance;

    private SavedCoordinateDAO savedCoordinateDAO;
    private LiveData<List<SavedCoordinate>> allSavedCoordinates;

    private WeatherCall weatherCall;
    private AQICall aqiCall;

    private List<SavedCoordinate> savedCoordinates;

    private List<Weather> weathers = new ArrayList<>();
    private MutableLiveData<List<Weather>> weatherList = new MutableLiveData<>();

    private List<AirQuality> airQualities = new ArrayList<>();
    private MutableLiveData<List<AirQuality>> airQualitiesList = new MutableLiveData<>();

    public WeatherRepository(Application application){
        Log.d(TAG, "WeatherRepository: created");
        WeatherDatabase database = WeatherDatabase.getInstance(application);
        savedCoordinateDAO = database.savedCoordinateDAO();
        getAllCoordinates();
        weatherCall = RetrofitService.createWeatherCall();
        aqiCall = RetrofitService.createAQICall();

    }

    public static synchronized WeatherRepository getInstance(Application application){
        if(instance == null){
            instance = new WeatherRepository(application);
        }
        return instance;
    }


    private void getAllCoordinates(){
        allSavedCoordinates = savedCoordinateDAO.selectAll();
    }

    public LiveData<List<SavedCoordinate>> getAllSavedCoordinates(){
        savedCoordinates = allSavedCoordinates.getValue();
        Log.d(TAG, "getAllSavedCoordinates: returning liveData of savedCoordinates");
        return allSavedCoordinates;
    }

    public void insert(SavedCoordinate savedCoordinate){
        new InsertCoordinateTask(savedCoordinateDAO).execute(savedCoordinate);
    }

    public void update(SavedCoordinate savedCoordinate){
        new UpdateCoordinateTask(savedCoordinateDAO).execute(savedCoordinate);
    }

    public void delete(SavedCoordinate savedCoordinate){
        new DeleteCoordinateTask(savedCoordinateDAO).execute(savedCoordinate);
    }

    public MutableLiveData<List<Weather>> getWeather(){
        savedCoordinates = allSavedCoordinates.getValue();
        Log.d(TAG, "getCurrentWeather: getting data from api" + savedCoordinates.size());

        for(SavedCoordinate c : savedCoordinates){
            Log.d(TAG, "getCurrentWeather: getting current weather");
            weatherCall.getWeather(c.getLat(),c.getLon()).enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(Call<Weather> call, Response<Weather> response) {
                    if(response.isSuccessful()){
                        weathers.add(response.body());
                        weatherList.postValue(weathers);
                        Log.d(TAG, "onResponse: " + response.body().getCurrently().getSummary());
                    }
                }

                @Override
                public void onFailure(Call<Weather> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });

        }
        return weatherList;
    }

    public MutableLiveData<List<AirQuality>> getAirQuality(){
        savedCoordinates = allSavedCoordinates.getValue();
        Log.d(TAG, "getCurrentAQI: getting data from api" + savedCoordinates.size());
        for(SavedCoordinate c : savedCoordinates){
            Log.d(TAG, "getCurrentAQI: getting current weather");
            aqiCall.getAirQuality(c.getLat(),c.getLon()).enqueue(new Callback<AirQuality>() {
                @Override
                public void onResponse(Call<AirQuality> call, Response<AirQuality> response) {
                    if(response.isSuccessful()){
                        airQualities.add(response.body());
                        airQualitiesList.postValue(airQualities);
                        Log.d(TAG, "onResponse: " + response.body().getData().aqi);
                    }
                }

                @Override
                public void onFailure(Call<AirQuality> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });

        }

        return airQualitiesList;
    }

    private static class InsertCoordinateTask extends AsyncTask<SavedCoordinate,Void,Void>{

        private SavedCoordinateDAO savedCoordinateDAO;

        private InsertCoordinateTask(SavedCoordinateDAO savedCoordinateDAO){
            this.savedCoordinateDAO = savedCoordinateDAO;
        }

        @Override
        protected Void doInBackground(SavedCoordinate... savedCoordinates) {
            savedCoordinateDAO.insert(savedCoordinates[0]);
            return null;
        }
    }

    private static class UpdateCoordinateTask extends AsyncTask<SavedCoordinate,Void,Void>{

        private SavedCoordinateDAO savedCoordinateDAO;

        private UpdateCoordinateTask(SavedCoordinateDAO savedCoordinateDAO){
            this.savedCoordinateDAO = savedCoordinateDAO;
        }

        @Override
        protected Void doInBackground(SavedCoordinate... savedCoordinates) {
            savedCoordinateDAO.update(savedCoordinates[0]);
            Log.d(TAG, "doInBackground: updating db");
            return null;
        }
    }

    private static class DeleteCoordinateTask extends AsyncTask<SavedCoordinate,Void,Void>{

        private SavedCoordinateDAO savedCoordinateDAO;

        private DeleteCoordinateTask(SavedCoordinateDAO savedCoordinateDAO){
            this.savedCoordinateDAO = savedCoordinateDAO;
        }

        @Override
        protected Void doInBackground(SavedCoordinate... savedCoordinates) {
            savedCoordinateDAO.delete(savedCoordinates[0]);
            return null;
        }
    }


}