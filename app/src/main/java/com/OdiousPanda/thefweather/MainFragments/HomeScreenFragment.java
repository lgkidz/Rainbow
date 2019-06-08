package com.OdiousPanda.thefweather.MainFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.OdiousPanda.thefweather.API.CurrentWeatherCall;
import com.OdiousPanda.thefweather.API.RetrofitService;
import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.TemperatureConverter;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreenFragment extends Fragment {
    private TextView mUrlDisplayTextView;
    private TextView mSearchResultsTextView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private SharedPreferences sharedPreferences;
    private String currentTempUnit;
    private CurrentWeather currentWeather;

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

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key_string), Context.MODE_PRIVATE);
        currentTempUnit = sharedPreferences.getString(getString(R.string.temp_unit_setting),null);
        if(currentTempUnit == null){
            currentTempUnit = getString(R.string.temp_unit_c);
        }

        return v;
    }

    public void makeQuery(String lat, String lon) {
        CurrentWeatherCall call = RetrofitService.createCurrentWeatherCall();
        call.getCurrentWeatherDataByCoordinate(lat,lon).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if(response.isSuccessful()){
                    currentWeather = response.body();
                    setTheText(currentTempUnit);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });
    }

    private void setTheText(String unit){
        String text = "";
        text += "city: " + currentWeather.getName() + "\n";
        text += "country code: " +currentWeather.getSys().getCountry() +"\n";
        text += "lat: " + currentWeather.getCoord().getLat() + ", lon: " + currentWeather.getCoord().getLon() +"\n";
        text += "weather: " + currentWeather.getWeather().get(0).getMain() +", " + currentWeather.getWeather().get(0).getDescription() +"\n";
        text += "temp min: " + TemperatureConverter.convertToUnitPretty(getActivity(),currentWeather.getMain().getTempMin(),unit) + ", temp max: " + TemperatureConverter.convertToUnitPretty(getActivity(),currentWeather.getMain().getTempMax(),unit) + " \n";
        text += "humidity: " + currentWeather.getMain().getHumidity() +"% \n";
        mSearchResultsTextView.setText(text);
    }

    public void updateTempUnit(String unit){
        setTheText(unit);
    }
}
