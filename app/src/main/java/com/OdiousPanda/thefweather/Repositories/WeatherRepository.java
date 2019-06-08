package com.OdiousPanda.thefweather.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.OdiousPanda.thefweather.API.CurrentWeatherCall;
import com.OdiousPanda.thefweather.API.ForecastWeatherCall;
import com.OdiousPanda.thefweather.API.RetrofitService;
import com.OdiousPanda.thefweather.DAOs.SavedCoordinateDAO;
import com.OdiousPanda.thefweather.Database.WeatherDatabase;
import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;
import com.OdiousPanda.thefweather.Model.Forecast.ForecastWeather;
import com.OdiousPanda.thefweather.Model.SavedCoordinate;

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

    private CurrentWeatherCall currentWeatherCall;
    private ForecastWeatherCall forecastWeatherCall;

    private List<ForecastWeather> forecastWeathers = new ArrayList<>();
    private List<CurrentWeather> currentWeathers = new ArrayList<>();
    private List<SavedCoordinate> savedCoordinates;

    private MutableLiveData<List<CurrentWeather>> currentWeatherList = new MutableLiveData<>();
    private MutableLiveData<List<ForecastWeather>> forecastWeatherList = new MutableLiveData<>();

    public WeatherRepository(Application application){
        Log.d(TAG, "WeatherRepository: created");
        WeatherDatabase database = WeatherDatabase.getInstance(application);
        savedCoordinateDAO = database.savedCoordinateDAO();
        getAllCoordinates();
        currentWeatherCall = RetrofitService.createCurrentWeatherCall();
        forecastWeatherCall = RetrofitService.createForecastWeatherCall();
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


    public MutableLiveData<List<CurrentWeather>> getCurrentWeather(){
        savedCoordinates = allSavedCoordinates.getValue();
        Log.d(TAG, "getCurrentWeather: getting data from api" + savedCoordinates.size());

        for(SavedCoordinate c : savedCoordinates){
            if(c.getName() == null || c.getName().trim().isEmpty()){
                Log.d(TAG, "getCurrentWeather: getting current weather1");
                currentWeatherCall.getCurrentWeatherDataByCoordinate(c.getLat(),c.getLon()).enqueue(new Callback<CurrentWeather>() {
                    @Override
                    public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                        if(response.isSuccessful()){
                            currentWeathers.add(response.body());
                            currentWeatherList.postValue(currentWeathers);
                            Log.d(TAG, "onResponse: " + response.body().getName());
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeather> call, Throwable t) {
                        Log.d(TAG, "onFailure: ");
                    }
                });
            }
            else{
                Log.d(TAG, "getCurrentWeather: getting current weather2");
                currentWeatherCall.getCurrentWeatherDataByCityName(c.getName()).enqueue(new Callback<CurrentWeather>() {
                    @Override
                    public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                        if(response.isSuccessful()){
                            currentWeathers.add(response.body());
                            currentWeatherList.setValue(currentWeathers);
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeather> call, Throwable t) {

                    }
                });
            }
        }
        return currentWeatherList;
    }


    public MutableLiveData<List<ForecastWeather>> getForecastWeather(){

        for(SavedCoordinate c : savedCoordinates){
            if(c.getName() == null || c.getName().trim().isEmpty()){
                forecastWeatherCall.getForecastWeatherByCityCoordinate(c.getLat(),c.getLon()).enqueue(new Callback<ForecastWeather>() {
                    @Override
                    public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                        if(response.isSuccessful()){
                            forecastWeathers.add(response.body());
                            forecastWeatherList.postValue(forecastWeathers);
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastWeather> call, Throwable t) {

                    }
                });
            }
            else{
                forecastWeatherCall.getForecastWeatherByCityName(c.getName()).enqueue(new Callback<ForecastWeather>() {
                    @Override
                    public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                        if(response.isSuccessful()){
                            forecastWeathers.add(response.body());
                            forecastWeatherList.setValue(forecastWeathers);
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastWeather> call, Throwable t) {

                    }
                });
            }
        }
        return forecastWeatherList;
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
