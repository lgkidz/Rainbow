package com.OdiousPanda.thefweather.MainFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;
import com.OdiousPanda.thefweather.Model.Forecast.ForecastWeather;
import com.OdiousPanda.thefweather.Model.Forecast.List;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.TemperatureConverter;
import com.google.gson.Gson;

public class DetailsFragment extends Fragment {
    public static DetailsFragment instance;
    private SharedPreferences sharedPreferences;

    private String currentTempUnit;
    private String currentDistanceUnit;
    private String currentSpeedUnit;
    private String currentPressureUnit;

    private ForecastWeather forecastWeather;
    private CurrentWeather currentWeather;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment getInstance() {
        if (instance == null){
            instance = new DetailsFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forecast, container, false);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key_string), Context.MODE_PRIVATE);

        return v;
    }


}
