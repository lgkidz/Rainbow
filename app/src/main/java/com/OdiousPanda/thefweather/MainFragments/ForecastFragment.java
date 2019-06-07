package com.OdiousPanda.thefweather.MainFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.OdiousPanda.thefweather.Model.Forecast.ForecastWeather;
import com.OdiousPanda.thefweather.Model.Forecast.List;
import com.OdiousPanda.thefweather.Model.SavedCoord;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.NetWorkUtils;
import com.OdiousPanda.thefweather.Utilities.TemperatureConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;

public class ForecastFragment extends Fragment {
    public static ForecastFragment instance;
    private Gson gson;
    private SharedPreferences sharedPreferences;

    private TextView forecastTv;
    private String fetched_results;
    private String currentTempUnit;

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

        gson = new Gson();
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key_string), Context.MODE_PRIVATE);
        currentTempUnit = sharedPreferences.getString(getString(R.string.temp_unit_setting),null);
        if(currentTempUnit == null){
            currentTempUnit = getString(R.string.temp_unit_c);
        }

        String savedCoordJson = sharedPreferences.getString(getString(R.string.saved_coord_pref_key),null);
        if(savedCoordJson != null){
            Type type = new TypeToken<SavedCoord>(){}.getType();
            SavedCoord savedCoord = gson.fromJson(savedCoordJson,type);
            makeQuery(null,String.valueOf(savedCoord.getLat()),String.valueOf(savedCoord.getLon()));
        }

        return v;
    }

    public void updateLocation(String lat, String lon){
        makeQuery(null,lat,lon);
    }

    private void makeQuery(String cityName, String lat, String lon) {
        Log.d("location", "makeQuery: lat:" + lat + " ,lon: " + lon);
        URL queryUrl = NetWorkUtils.buildURL(NetWorkUtils.TYPE_FORECAST,cityName,lat,lon);
        new WeatherQueryTask().execute(queryUrl);
    }

    public class WeatherQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetWorkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null && !results.equals("")) {
                fetched_results = results;
                try{
                    setTheText(currentTempUnit);
                } catch (Exception e){
                    Log.d("location", "onPostExecute: " + e.toString());
                }
            } else {
                Log.d("location", "onPostExecute: something wrong");
            }
        }
    }

    private void setTheText(String unit){
        Gson gson = new Gson();
        Type type = new TypeToken<ForecastWeather>(){}.getType();
        ForecastWeather forecastWeather = gson.fromJson(fetched_results,type);
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
