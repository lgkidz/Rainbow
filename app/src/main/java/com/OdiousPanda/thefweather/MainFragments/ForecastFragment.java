package com.OdiousPanda.thefweather.MainFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.OdiousPanda.thefweather.Model.Forecast.ForecastWeather;
import com.OdiousPanda.thefweather.Model.Forecast.List;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.TemperatureConverter;
import com.google.gson.Gson;

public class ForecastFragment extends Fragment {
    public static ForecastFragment instance;
    private Gson gson;
    private SharedPreferences sharedPreferences;

    private TextView forecastTv;
    private String fetched_results;
    private String currentTempUnit;
    private ForecastWeather forecastWeather;

    public ForecastFragment() {
        // Required empty public constructor
    }

    public static ForecastFragment getInstance() {
        if (instance == null){
            instance = new ForecastFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forecast, container, false);
        forecastTv = v.findViewById(R.id.forecast_tv);

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key_string), Context.MODE_PRIVATE);
        currentTempUnit = sharedPreferences.getString(getString(R.string.temp_unit_setting),null);
        if(currentTempUnit == null){
            currentTempUnit = getString(R.string.temp_unit_c);
        }

        return v;
    }

    private void makeQuery(String lat, String lon) {

    }

    private void setTheText(String unit){
        String text = "";
        text += "City: " + forecastWeather.getCity().getName() + "\n"
                + "country code: " + forecastWeather.getCity().getCountry() + "\n"
                + "population: " + forecastWeather.getCity().getPopulation() + "\n"
                + "coord: lat:" + forecastWeather.getCity().getCoord().getLat() + ", lon: " + forecastWeather.getCity().getCoord().getLon() + "\n"
                + "weather: \n";
        for(List l : forecastWeather.getList()){
            String x = "";
            x += "time: " + l.getDtTxt() +"\n";
            x += "condition: " + l.getWeather().get(0).getMain() + "\n";
            x += "temp: " + TemperatureConverter.convertToUnitPretty(getActivity(),l.getMain().getTemp(),unit) +"\n";
            x += "humidity: " + l.getMain().getHumidity() +"% \n\n";
            text += x;
        }
        forecastTv.setText(text);
    }

    public void updateTempUnit(String unit){
        setTheText(unit);
    }

}
