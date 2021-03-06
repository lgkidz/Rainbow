package com.OdiousPanda.rainbow.Repositories;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.OdiousPanda.rainbow.API.AQICall;
import com.OdiousPanda.rainbow.API.RetrofitService;
import com.OdiousPanda.rainbow.API.WeatherCall;
import com.OdiousPanda.rainbow.DAOs.CoordinateDAO;
import com.OdiousPanda.rainbow.DataModel.AQI.AirQuality;
import com.OdiousPanda.rainbow.DataModel.Coordinate;
import com.OdiousPanda.rainbow.DataModel.LocationData;
import com.OdiousPanda.rainbow.DataModel.Weather.Weather;
import com.OdiousPanda.rainbow.Database.WeatherDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private static final String TAG = "weatherA";
    private static WeatherRepository instance;
    private String locale;
    private CoordinateDAO coordinateDAO;
    private LiveData<List<Coordinate>> allSavedCoordinates;

    private WeatherCall weatherCall;
    private AQICall aqiCall;

    private List<Coordinate> coordinates = new ArrayList<>();
    private List<LocationData> locations = new ArrayList<>();
    private MutableLiveData<List<LocationData>> locationDataList = new MutableLiveData<>();
    private MutableLiveData<AirQuality> airQualityByCoordinate = new MutableLiveData<>();

    private WeatherRepository(Context context) {
        Log.d(TAG, "WeatherRepository: created");
        locale = context.getResources().getConfiguration().locale.getLanguage();
        if (!locale.equals("vi")) {
            locale = "en";
        }
        WeatherDatabase database = WeatherDatabase.getInstance(context);
        coordinateDAO = database.savedCoordinateDAO();
        getAllCoordinates();
        weatherCall = RetrofitService.createWeatherCall();
        aqiCall = RetrofitService.createAQICall();
    }

    public static synchronized WeatherRepository getInstance(Context context) {
        if (instance == null) {
            instance = new WeatherRepository(context);
        }
        return instance;
    }

    private void getAllCoordinates() {
        allSavedCoordinates = coordinateDAO.selectAll();
    }

    public LiveData<List<Coordinate>> getAllSavedCoordinates() {
        coordinates = allSavedCoordinates.getValue();
        Log.d(TAG, "getAllSavedCoordinates: returning liveData of coordinates");
        return allSavedCoordinates;
    }

    public void insert(Coordinate coordinate) {
        new InsertCoordinateTask(coordinateDAO).execute(coordinate);
    }

    public void update(Coordinate coordinate) {
        new UpdateCoordinateTask(coordinateDAO).execute(coordinate);
    }

    public void delete(Coordinate coordinate) {
        Log.d(TAG, "weatherRepo delete: calling async task");
        new DeleteCoordinateTask(coordinateDAO).execute(coordinate);
    }

    public MutableLiveData<List<LocationData>> getLocationWeathers() {
        locations.clear();
        coordinates = allSavedCoordinates.getValue();
        assert coordinates != null;
        Log.d(TAG, "getCurrentWeather: getting data from api: total: " + coordinates.size());
        for (final Coordinate coordinate : coordinates) {
            weatherCall.getWeather(coordinate.getLat(), coordinate.getLon(), locale).enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {
                    if (response.isSuccessful()) {
                        LocationData currentLocation = new LocationData(coordinate);
                        currentLocation.setWeather(response.body());
                        locations.add(currentLocation);
                        Collections.sort(locations, new Comparator<LocationData>() {
                            @Override
                            public int compare(LocationData o1, LocationData o2) {
                                return o1.getCoordinate().getId() - o2.getCoordinate().getId();
                            }
                        });
                        locationDataList.setValue(locations);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Weather> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        }
        return locationDataList;
    }

    public MutableLiveData<AirQuality> getAirQualityByCoordinate(Coordinate coordinate) {
        Log.d(TAG, "getCurrentAQI: getting current AQI " + coordinate.getLat() + ", " + coordinate.getLon());
        aqiCall.getAirQuality(coordinate.getLat(), coordinate.getLon()).enqueue(new Callback<AirQuality>() {
            @Override
            public void onResponse(@NonNull Call<AirQuality> call, @NonNull Response<AirQuality> response) {
                if (response.isSuccessful()) {
                    airQualityByCoordinate.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AirQuality> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
        return airQualityByCoordinate;
    }

    private static class InsertCoordinateTask extends AsyncTask<Coordinate, Void, Void> {

        private CoordinateDAO coordinateDAO;

        private InsertCoordinateTask(CoordinateDAO coordinateDAO) {
            this.coordinateDAO = coordinateDAO;
        }

        @Override
        protected Void doInBackground(Coordinate... coordinates) {
            coordinateDAO.insert(coordinates[0]);
            return null;
        }

    }

    private static class UpdateCoordinateTask extends AsyncTask<Coordinate, Void, Void> {

        private CoordinateDAO coordinateDAO;

        private UpdateCoordinateTask(CoordinateDAO coordinateDAO) {
            this.coordinateDAO = coordinateDAO;
        }

        @Override
        protected Void doInBackground(Coordinate... coordinates) {
            coordinateDAO.update(coordinates[0]);
            Log.d(TAG, "doInBackground: updating db");
            return null;
        }
    }

    private static class DeleteCoordinateTask extends AsyncTask<Coordinate, Void, Void> {

        private CoordinateDAO coordinateDAO;

        private DeleteCoordinateTask(CoordinateDAO coordinateDAO) {
            this.coordinateDAO = coordinateDAO;
        }

        @Override
        protected Void doInBackground(Coordinate... coordinates) {
            Log.d(TAG, "Delete Async task: doInBackground: deleting");
            coordinateDAO.delete(coordinates[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
