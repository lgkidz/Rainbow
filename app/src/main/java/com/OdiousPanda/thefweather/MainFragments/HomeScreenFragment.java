package com.OdiousPanda.thefweather.MainFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;
import com.OdiousPanda.thefweather.Model.SavedCoord;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.NetWorkUtils;
import com.OdiousPanda.thefweather.Utilities.TemperatureConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;

public class HomeScreenFragment extends Fragment {
    private TextView mUrlDisplayTextView;
    private TextView mSearchResultsTextView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private Gson gson;
    private SharedPreferences sharedPreferences;
    private String currentTempUnit;
    private String fetched_results;

    public static HomeScreenFragment instance;

    public static HomeScreenFragment getInstance(){
        if (instance == null){
            instance = new HomeScreenFragment();
        }

        return instance;
    }

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_screen, container, false);

        mUrlDisplayTextView = v.findViewById(R.id.tv_url_display);
        mSearchResultsTextView = v.findViewById(R.id.tv_github_search_results_json);
        mErrorMessageDisplay = v.findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = v.findViewById(R.id.pb_loading_indicator);

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

    private void makeQuery(String cityName, String lat, String lon) {
        URL queryUrl = NetWorkUtils.buildURL(NetWorkUtils.TYPE_CURRENT_WEATHER,cityName,lat,lon);
        mUrlDisplayTextView.setText(queryUrl.toString());
        new WeatherQueryTask().execute(queryUrl);
    }

    private void showJsonDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mSearchResultsTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    public class WeatherQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
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

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (results != null && !results.equals("")) {
                showJsonDataView();
                fetched_results = results;
                try{
                    setTheText(currentTempUnit);
                } catch (Exception e){

                }
            } else {
                showErrorMessage();
            }
        }
    }

    private void setTheText(String unit){
        Gson gson = new Gson();
        Type type = new TypeToken<CurrentWeather>(){}.getType();
        CurrentWeather currentWeather = gson.fromJson(fetched_results,type);
        String text = "";
        text += "city: " + currentWeather.getName() + "\n";
        text += "country code: " +currentWeather.getSys().getCountry() +"\n";
        text += "lat: " + currentWeather.getCoord().getLat() + ", lon: " + currentWeather.getCoord().getLon() +"\n";
        text += "weather: " + currentWeather.getWeather().get(0).getMain() +", " + currentWeather.getWeather().get(0).getDescription() +"\n";
        text += "temp min: " + TemperatureConverter.convertToUnitPretty(getActivity(),currentWeather.getMain().getTempMin(),unit) + ", temp max: " + TemperatureConverter.convertToUnitPretty(getActivity(),currentWeather.getMain().getTempMax(),unit) + " \n";
        text += "humidity: " + currentWeather.getMain().getHumidity() +"% \n";
        mSearchResultsTextView.setText(text);
    }

    public void updateLocation(String lat, String lon){
        makeQuery(null,lat,lon);
    }

    public void updateTempUnit(String unit){
        setTheText(unit);
    }
}
