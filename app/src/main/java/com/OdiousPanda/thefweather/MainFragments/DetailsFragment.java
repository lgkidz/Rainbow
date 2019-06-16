package com.OdiousPanda.thefweather.MainFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.OdiousPanda.thefweather.Adapters.ForecastAdapter;
import com.OdiousPanda.thefweather.CustomDialogs.AirQualityDialog;
import com.OdiousPanda.thefweather.Model.AQI.AirQuality;
import com.OdiousPanda.thefweather.Model.Weather.Weather;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.MyColorUtil;
import com.OdiousPanda.thefweather.Utilities.UnitConverter;

public class DetailsFragment extends Fragment {
    public static DetailsFragment instance;
    private SharedPreferences sharedPreferences;

    private Weather currentWeather;
    private AirQuality airQuality;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment getInstance() {
        if (instance == null){
            instance = new DetailsFragment();
        }
        return instance;
    }

    private TextView tvHeading;
    private TextView tvRealFeel;
    private TextView tvRealFeelTitle;
    private TextView tvUV;
    private TextView tvUVTitle;
    private TextView tvHumidity;
    private TextView tvHumidityTitle;
    private TextView tvVisibility;
    private TextView tvVisibilityTitle;
    private TextView tvPressure;
    private TextView tvPressureTitle;
    private TextView tvAqiTitle;
    private TextView tvAqi;
    private TextView tvAqiSummary;
    private TextView tvForecast;
    private ConstraintLayout layoutRealFeel;
    private LinearLayout layoutHumidity;
    private LinearLayout layoutUV;
    private LinearLayout layoutVisibility;
    private LinearLayout layoutPressure;
    private ConstraintLayout layoutAqi;
    private LinearLayout layoutForecast;
    private ImageView aqiInfo;
    private RecyclerView rvForecast;

    private int headingColor = Color.argb(255,0,0,0);
    private int textColor = Color.argb(255,0,0,0);
    private int layoutColor = Color.argb(0,0,0,0);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key_string), Context.MODE_PRIVATE);

        initViews(v);

        return v;
    }

    private void initViews(View v){
        rvForecast = v.findViewById(R.id.rv_forecast);
        rvForecast.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        tvHeading = v.findViewById(R.id.tv_heading);
        tvRealFeel = v.findViewById(R.id.tv_tempRealFeel);
        tvRealFeelTitle = v.findViewById(R.id.tv_tempRealFeelTitle);
        tvUV = v.findViewById(R.id.tv_Uv);
        tvUVTitle = v.findViewById(R.id.tv_UVTitle);
        tvHumidity = v.findViewById(R.id.tv_humidity);
        tvHumidityTitle = v.findViewById(R.id.tv_humidityTitle);
        tvVisibility = v.findViewById(R.id.tv_visibility);
        tvVisibilityTitle = v.findViewById(R.id.tv_visibilityTitle);
        tvPressure = v.findViewById(R.id.tv_pressure);
        tvPressureTitle = v.findViewById(R.id.tv_pressureTitle);
        tvAqi = v.findViewById(R.id.tv_aqi);
        tvAqiSummary = v.findViewById(R.id.tv_aqi_summary);
        tvAqiTitle = v.findViewById(R.id.tv_aqi_title);
        tvForecast = v.findViewById(R.id.tv_forecast_title);

        layoutRealFeel = v.findViewById(R.id.layoutRealFeel);
        layoutUV = v.findViewById(R.id.layoutUV);
        layoutHumidity = v.findViewById(R.id.layoutHumidity);
        layoutVisibility = v.findViewById(R.id.layoutVisibility);
        layoutPressure = v.findViewById(R.id.layoutPressure);
        layoutAqi = v.findViewById(R.id.layoutAqi);
        layoutForecast = v.findViewById(R.id.layout_forecast);

        aqiInfo = v.findViewById(R.id.icon_aqi_info);
        aqiInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AirQualityDialog airQualityDialog = new AirQualityDialog(getActivity(),airQuality);
                airQualityDialog.showDialog();
            }
        });

        if(currentWeather != null){
            updateUnit();
        }
    }

    public void updateColorTheme(int[] argb){
        headingColor = MyColorUtil.blackOrWhiteOf(argb);
        tvHeading.setTextColor(headingColor);
        int r = (int) (argb[1] * 0.8);
        int g = (int) (argb[2] * 0.8);
        int b = (int) (argb[3] * 0.75);
        textColor = MyColorUtil.blackOrWhiteOf(new int[]{255,r,g,b});
        layoutColor = Color.argb(255,r,g,b);
        colorThoseLayout();
        colorThoseTextView();
        if(textColor == Color.WHITE){
            aqiInfo.setImageResource(R.drawable.ic_info_w);
        }
        else{
            aqiInfo.setImageResource(R.drawable.ic_info_b);
        }
    }

    private void colorThoseTextView(){
        tvHeading.setTextColor(headingColor);
        tvRealFeel.setTextColor(textColor);
        tvRealFeelTitle.setTextColor(textColor);
        tvUV.setTextColor(textColor);
        tvUVTitle.setTextColor(textColor);
        tvHumidity.setTextColor(textColor);
        tvHumidityTitle.setTextColor(textColor);
        tvVisibility.setTextColor(textColor);
        tvVisibilityTitle.setTextColor(textColor);
        tvPressure.setTextColor(textColor);
        tvPressureTitle.setTextColor(textColor);
        tvAqi.setTextColor(textColor);
        tvAqiSummary.setTextColor(textColor);
        tvAqiTitle.setTextColor(textColor);
        tvForecast.setTextColor(textColor);
    }

    private void colorThoseLayout(){
        layoutRealFeel.setBackgroundColor(layoutColor);
        layoutUV.setBackgroundColor(layoutColor);
        layoutHumidity.setBackgroundColor(layoutColor);
        layoutVisibility.setBackgroundColor(layoutColor);
        layoutPressure.setBackgroundColor(layoutColor);
        layoutAqi.setBackgroundColor(layoutColor);
        layoutForecast.setBackgroundColor(layoutColor);
    }

    public void updateUnit(){
        String currentTempUnit = sharedPreferences.getString(getString(R.string.pref_temp),getString(R.string.temp_setting_degree_c));
        String currentDistanceUnit = sharedPreferences.getString(getString(R.string.pref_distance),getString(R.string.km));
        String currentSpeedUnit = sharedPreferences.getString(getString(R.string.pref_speed),getString(R.string.kmph));
        String currentPressureUnit = sharedPreferences.getString(getString(R.string.pref_pressure),getString(R.string.psi));
        tvRealFeel.setText(UnitConverter.convertToTemperatureUnit(currentWeather.getCurrently().getApparentTemperature(),currentTempUnit));
        String uvSummary = "";
        if(currentWeather.getCurrently().getUvIndex() == 0){
            uvSummary = "Where's the sun?";
        }
        else if(currentWeather.getCurrently().getUvIndex() < 3){
            uvSummary = " (Low)";
        }else if(currentWeather.getCurrently().getUvIndex() < 6){
            uvSummary = " (Moderate)";
        }
        else if(currentWeather.getCurrently().getUvIndex() < 8){
            uvSummary = " (High)";
        }
        else if(currentWeather.getCurrently().getUvIndex() < 11){
            uvSummary = " (High af)";
        }
        else {
            uvSummary = " (Extreme)";
        }
        tvUV.setText((Math.round(currentWeather.getCurrently().getUvIndex())==0?"":Math.round(currentWeather.getCurrently().getUvIndex())) + uvSummary);
        tvHumidity.setText(Math.round(currentWeather.getCurrently().getHumidity() * 100) + "%");
        tvPressure.setText(UnitConverter.convertToPressureUnit(currentWeather.getCurrently().getPressure(),currentPressureUnit));
        if(currentPressureUnit.equals(getString(R.string.depression_unit))){
            tvPressureTitle.setText(getString(R.string.pressure_title_alt));
        }
        else {
            tvPressureTitle.setText(getString(R.string.pressure_title));
        }
        tvVisibility.setText(UnitConverter.convertToDistanceUnit(currentWeather.getCurrently().getVisibility(),currentDistanceUnit));
        ForecastAdapter adapter = new ForecastAdapter(getActivity(),sharedPreferences,currentWeather.getDaily(),textColor);
        rvForecast.setAdapter(adapter);
    }

    public void updateData(Weather weather){
        
        currentWeather = weather;
        String currentTempUnit = sharedPreferences.getString(getString(R.string.pref_temp),getString(R.string.temp_setting_degree_c));
        String currentDistanceUnit = sharedPreferences.getString(getString(R.string.pref_distance),getString(R.string.km));
        String currentSpeedUnit = sharedPreferences.getString(getString(R.string.pref_speed),getString(R.string.kmph));
        String currentPressureUnit = sharedPreferences.getString(getString(R.string.pref_pressure),getString(R.string.psi));
        tvRealFeel.setText(UnitConverter.convertToTemperatureUnit(currentWeather.getCurrently().getApparentTemperature(),currentTempUnit));
        String uvSummary = "";
        if(currentWeather.getCurrently().getUvIndex() == 0){
            uvSummary = "Where's the sun?";
        }
        else if(currentWeather.getCurrently().getUvIndex() < 3){
            uvSummary = " (Low)";
        }else if(currentWeather.getCurrently().getUvIndex() < 6){
            uvSummary = " (Moderate)";
        }
        else if(currentWeather.getCurrently().getUvIndex() < 8){
            uvSummary = " (High)";
        }
        else if(currentWeather.getCurrently().getUvIndex() < 11){
            uvSummary = " (High af)";
        }
        else {
            uvSummary = " (Extreme)";
        }
        tvUV.setText((Math.round(currentWeather.getCurrently().getUvIndex())==0?"":Math.round(currentWeather.getCurrently().getUvIndex())) + uvSummary);
        tvHumidity.setText(Math.round(currentWeather.getCurrently().getHumidity() * 100) + "%");
        tvPressure.setText(UnitConverter.convertToPressureUnit(currentWeather.getCurrently().getPressure(),currentPressureUnit));
        if(currentPressureUnit.equals(getString(R.string.depression_unit))){
            tvPressureTitle.setText("Depression level");
        }
        tvVisibility.setText(UnitConverter.convertToDistanceUnit(currentWeather.getCurrently().getVisibility(),currentDistanceUnit));
        ForecastAdapter adapter = new ForecastAdapter(getActivity(),sharedPreferences,currentWeather.getDaily(),textColor);
        rvForecast.setAdapter(adapter);
    }

    public void updateAqi(AirQuality air){
        airQuality = air;
        int aqi = Math.round(airQuality.getData().aqi);
        tvAqi.setText( aqi+ "");
        String aqiSummary = "";
        if(aqi < 51){
            aqiSummary = "Hmmmm, fresh air!";
        }
        else if(aqi < 101){
            aqiSummary = "The air is quite okay.";
        }
        else if(aqi < 151){
            aqiSummary = "A bit unhealthy for those special snowflakes.";
        }
        else if(aqi < 201){
            aqiSummary = "Unhealthy! Inhale more for diseases.";
        }
        else if(aqi < 301){
            aqiSummary = "Unhealthy as fuck! Lung cancer awaits you outside.";
        }
        else{
            aqiSummary = "Living in chernobyl would be more healthy.";
        }
        tvAqiSummary.setText(aqiSummary);

    }
}
