package com.OdiousPanda.thefweather.MainFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OdiousPanda.thefweather.Adapters.ForecastAdapter;
import com.OdiousPanda.thefweather.DataModel.AQI.AirQuality;
import com.OdiousPanda.thefweather.DataModel.Weather.Weather;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.MyColorUtil;
import com.OdiousPanda.thefweather.Utilities.PreferencesUtil;
import com.OdiousPanda.thefweather.Utilities.UnitConverter;

import java.util.Objects;

public class DetailsFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static DetailsFragment instance;

    private Weather currentWeather;
    private AirQuality airQuality;
    private TextView tvHeading;
    private TextView tvLocation;
    private ImageView icLocation;
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
    private TextView tvWindTitle;
    private TextView tvWindSpeed;
    private TextView tvWindDirection;
    //    private TextView tvNextHoursTitle;
    private ConstraintLayout layoutLocation;
    private ConstraintLayout layoutRealFeel;
    private LinearLayout layoutHumidity;
    private LinearLayout layoutUV;
    private LinearLayout layoutVisibility;
    private LinearLayout layoutPressure;
    private ConstraintLayout layoutAqi;
    private ImageView aqiInfo;
    private ProgressBar aqiPb;
    private RecyclerView rvForecast;
    private ImageView windmillWings;
    private CardView aqiDetailLayout;
    private LinearLayout airQualityIndexScale;
    private ImageView aqiIndexIndicator;
    private TextView tvDetailAqiIndex;
    private TextView tvDetailAqiLevel;
    private TextView tvDetailAqiDes;
    private ImageView btnAqiDetailClose;
    private View aqiDetailLayoutBg;
    public boolean aqiDetailShowing = false;
    private int headingColor = Color.argb(255, 0, 0, 0);
    private int textColor = Color.argb(255, 0, 0, 0);
    private int layoutColor = Color.argb(0, 0, 0, 0);
    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment getInstance() {
        if (instance == null) {
            instance = new DetailsFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        initViews(v);

        return v;
    }

    private void initViews(View v) {
        rvForecast = v.findViewById(R.id.rv_forecast);
        rvForecast.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        tvHeading = v.findViewById(R.id.tv_heading);
        tvLocation = v.findViewById(R.id.tv_location);
        icLocation = v.findViewById(R.id.ic_location);
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
        tvWindTitle = v.findViewById(R.id.wind_title);
        tvWindSpeed = v.findViewById(R.id.wind_speed);
        tvWindDirection = v.findViewById(R.id.wind_direction);
        windmillWings = v.findViewById(R.id.windmill_wings);
//        tvNextHoursTitle = v.findViewById(R.id.tv_next_hours_title);

        layoutLocation = v.findViewById(R.id.layoutLocation);
        layoutRealFeel = v.findViewById(R.id.layoutRealFeel);
        layoutUV = v.findViewById(R.id.layoutUV);
        layoutHumidity = v.findViewById(R.id.layoutHumidity);
        layoutVisibility = v.findViewById(R.id.layoutVisibility);
        layoutPressure = v.findViewById(R.id.layoutPressure);
        layoutAqi = v.findViewById(R.id.layoutAqi);
        aqiPb = v.findViewById(R.id.aqi_progress_bar);

        aqiDetailLayout = v.findViewById(R.id.aqi_detail_layout);
        tvDetailAqiIndex = v.findViewById(R.id.tv_aqi_index);
        tvDetailAqiLevel = v.findViewById(R.id.tv_aqi_level);
        tvDetailAqiDes = v.findViewById(R.id.tv_aqi_des);
        aqiIndexIndicator = v.findViewById(R.id.aqi_index_indicator);
        airQualityIndexScale = v.findViewById(R.id.index_scale);
        btnAqiDetailClose = v.findViewById(R.id.btn_close_aqi_detail);
        aqiDetailLayoutBg = v.findViewById(R.id.aqi_detail_layout_bg);
        boolean isExplicit = PreferencesUtil.isExplicit(Objects.requireNonNull(getActivity()));
        updateRealFeedTitle(isExplicit);

        aqiInfo = v.findViewById(R.id.icon_aqi_info);
        aqiInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(airQuality != null && !aqiDetailShowing) {
                    showAqiDetailDialog();
                }
            }
        });
        btnAqiDetailClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAqiDetailDialog();
            }
        });

        if (currentWeather != null) {
            updateUnit();
        }
    }

    public void updateColorTheme(int[] argb) {
        headingColor = MyColorUtil.blackOrWhiteOf(argb);
        tvHeading.setTextColor(headingColor);
        int r = (int) (argb[1] * 0.8);
        int g = (int) (argb[2] * 0.8);
        int b = (int) (argb[3] * 0.75);
        textColor = MyColorUtil.blackOrWhiteOf(new int[]{255, r, g, b});
        layoutColor = Color.argb(255, r, g, b);
        colorThoseLayout();
        colorThoseTextView();
        if (textColor == Color.WHITE) {
            aqiInfo.setImageResource(R.drawable.ic_info_w);
        } else {
            aqiInfo.setImageResource(R.drawable.ic_info_b);
        }

        if (currentWeather != null) {
            update7dayForeCastData();
        }
    }

    private void colorThoseTextView() {
        tvHeading.setTextColor(headingColor);
        tvLocation.setTextColor(textColor);
        if (textColor == Color.WHITE) {
            icLocation.setImageResource(R.drawable.ic_round_location_w);
        } else {
            icLocation.setImageResource(R.drawable.ic_round_location_b);
        }
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
        tvForecast.setTextColor(headingColor);
        tvWindTitle.setTextColor(headingColor);
        tvWindSpeed.setTextColor(headingColor);
        tvWindDirection.setTextColor(headingColor);
//        tvNextHoursTitle.setTextColor(headingColor);
    }

    private void colorThoseLayout() {
        layoutLocation.setBackgroundColor(layoutColor);
        layoutRealFeel.setBackgroundColor(layoutColor);
        layoutUV.setBackgroundColor(layoutColor);
        layoutHumidity.setBackgroundColor(layoutColor);
        layoutVisibility.setBackgroundColor(layoutColor);
        layoutPressure.setBackgroundColor(layoutColor);
        layoutAqi.setBackgroundColor(layoutColor);
    }

    void updateUnit() {
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(Objects.requireNonNull(getActivity()));
        String currentDistanceUnit = PreferencesUtil.getDistanceUnit(getActivity());
        String currentSpeedUnit = PreferencesUtil.getSpeedUnit(getActivity());
        String currentPressureUnit = PreferencesUtil.getPressureUnit(getActivity());
        boolean isExplicit = PreferencesUtil.isExplicit(getActivity());

        updateRealFeelTemperature(currentTempUnit);
        updateRealFeedTitle(isExplicit);
        updatePressureData(currentPressureUnit);
        updateVisibilityData(currentDistanceUnit);
        updateWindSpeedData(currentSpeedUnit);
        update7dayForeCastData();
    }

    public void updateData(Weather weather) {
        currentWeather = weather;
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(Objects.requireNonNull(getActivity()));
        String currentDistanceUnit = PreferencesUtil.getDistanceUnit(getActivity());
        String currentSpeedUnit = PreferencesUtil.getSpeedUnit(getActivity());
        String currentPressureUnit = PreferencesUtil.getPressureUnit(getActivity());

        updateRealFeelTemperature(currentTempUnit);
        updateUvData();
        updateHumidityData();
        updatePressureData(currentPressureUnit);
        updateVisibilityData(currentDistanceUnit);
        updateWindSpeedData(currentSpeedUnit);
        update7dayForeCastData();
    }

    private void updateRealFeedTitle(boolean isExplicit) {
        if (isExplicit) {
            tvRealFeelTitle.setText(getText(R.string.real_feel_title_explicit));
        } else {
            tvRealFeelTitle.setText(getText(R.string.real_feel_title));
        }
    }

    private void update7dayForeCastData() {
        ForecastAdapter adapter = new ForecastAdapter(getActivity(), currentWeather.getDaily(), headingColor);
        rvForecast.setAdapter(adapter);
    }

    private void updateWindSpeedData(String currentSpeedUnit) {
        tvWindSpeed.setText(UnitConverter.convertToSpeedUnit(currentWeather.getCurrently().getWindSpeed(), currentSpeedUnit));
        float windSpeedMps = UnitConverter.toMeterPerSecond(currentWeather.getCurrently().getWindSpeed());
        String windDirectionText = "";
        if (windSpeedMps > 0) {
            float windBearing = currentWeather.getCurrently().getWindBearing();
            if ((windBearing >= 0 && windBearing < 11.25) || (windBearing >= 348.75 && windBearing <= 360)) {
                windDirectionText = getString(R.string.wind_north);
            } else if (windBearing >= 11.25 && windBearing < 33.75) {
                windDirectionText = getString(R.string.wind_north) + " - " + getString(R.string.wind_north_east);
            } else if (windBearing >= 33.75 && windBearing < 56.25) {
                windDirectionText = getString(R.string.wind_north_east);
            } else if (windBearing >= 56.25 && windBearing < 78.75) {
                windDirectionText = getString(R.string.wind_east) + " - " + getString(R.string.wind_north_east);
            } else if (windBearing >= 78.75 && windBearing < 101.25) {
                windDirectionText = getString(R.string.wind_east);
            } else if (windBearing >= 101.25 && windBearing < 123.75) {
                windDirectionText = getString(R.string.wind_east) + " - " + getString(R.string.wind_south_east);
            } else if (windBearing >= 123.75 && windBearing < 146.25) {
                windDirectionText = getString(R.string.wind_south_east);
            } else if (windBearing >= 146.25 && windBearing < 168.75) {
                windDirectionText = getString(R.string.wind_south) + " - " + getString(R.string.wind_south_east);
            } else if (windBearing >= 168.75 && windBearing < 191.25) {
                windDirectionText = getString(R.string.wind_south);
            } else if (windBearing >= 191.25 && windBearing < 213.75) {
                windDirectionText = getString(R.string.wind_south) + " - " + getString(R.string.wind_south_west);
            } else if (windBearing >= 213.75 && windBearing < 236.25) {
                windDirectionText = getString(R.string.wind_south_west);
            } else if (windBearing >= 236.25 && windBearing < 258.75) {
                windDirectionText = getString(R.string.wind_west) + " - " + getString(R.string.wind_south_west);
            } else if (windBearing >= 258.75 && windBearing < 281.25) {
                windDirectionText = getString(R.string.wind_west);
            } else if (windBearing >= 281.25 && windBearing < 303.75) {
                windDirectionText = getString(R.string.wind_west) + " - " + getString(R.string.wind_north_west);
            } else if (windBearing >= 303.75 && windBearing <= 326.25) {
                windDirectionText = getString(R.string.wind_north_west);
            } else if (windBearing >= 326.25 && windBearing < 348.75) {
                windDirectionText = getString(R.string.wind_north) + " - " + getString(R.string.wind_north_west);
            }
        }
        tvWindDirection.setText(windDirectionText);
        //set Windmill rotating animation
        windmillWings.clearAnimation();
        double windmillWingsDiameter = 10; //diameter in meter

        double roundPerSec = windSpeedMps / (Math.PI * windmillWingsDiameter);
        int duration = (int) (1000 / (roundPerSec));
        RotateAnimation rotate = new RotateAnimation(0, -360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setDuration(duration);
        rotate.setRepeatCount(Animation.INFINITE);
        windmillWings.startAnimation(rotate);
    }

    private void updateRealFeelTemperature(String currentTempUnit) {
        tvRealFeel.setText(UnitConverter.convertToTemperatureUnit(currentWeather.getCurrently().getApparentTemperature(), currentTempUnit));
    }

    private void updateVisibilityData(String currentDistanceUnit) {
        tvVisibility.setText(UnitConverter.convertToDistanceUnit(currentWeather.getCurrently().getVisibility(), currentDistanceUnit));
    }

    private void updatePressureData(String currentPressureUnit) {
        tvPressure.setText(UnitConverter.convertToPressureUnit(getActivity(),currentWeather.getCurrently().getPressure(), currentPressureUnit));
        if (currentPressureUnit.equals(getString(R.string.depression_unit))) {
            tvPressureTitle.setText(getString(R.string.depression_level_title));
        }else {
            tvPressureTitle.setText(getString(R.string.pressure_title));
        }
    }

    private void updateHumidityData() {
        String humidityText = Math.round(currentWeather.getCurrently().getHumidity() * 100) + "%";
        tvHumidity.setText(humidityText);
    }

    private void updateUvData() {
        String uvSummary = " ";
        if (currentWeather.getCurrently().getUvIndex() == 0) {
            uvSummary += getString(R.string.uv_summary_0);
        } else if (currentWeather.getCurrently().getUvIndex() < 3) {
            uvSummary += getString(R.string.uv_summary_3);
        } else if (currentWeather.getCurrently().getUvIndex() < 6) {
            uvSummary += getString(R.string.uv_summary_6);
        } else if (currentWeather.getCurrently().getUvIndex() < 8) {
            uvSummary  += getString(R.string.uv_summary_8);
        } else if (currentWeather.getCurrently().getUvIndex() < 11) {
            uvSummary += getString(R.string.uv_summary_11);
        } else {
            uvSummary += getString(R.string.uv_summary_11_above);
        }
        String uvValueText = (Math.round(currentWeather.getCurrently().getUvIndex()) == 0 ? "" : Math.round(currentWeather.getCurrently().getUvIndex())) + uvSummary;
        tvUV.setText(uvValueText);
    }

    public void updateCurrentLocationName(String locationName) {
        if(this.tvLocation != null){
            this.tvLocation.setText(locationName);
        }
    }

    public void updateAqi(AirQuality air) {
        aqiPb.setVisibility(View.GONE);
        airQuality = air;
        final float aqi = airQuality.getData().aqi;
        tvAqi.setText(String.valueOf((int)aqi));
        tvDetailAqiIndex.setText(String.valueOf((int)aqi));
        String aqiSummary = "";
        Context context = getActivity();
        assert context != null;
        if (aqi <= 50) {
            aqiSummary = getString(R.string.r_aqi_good_des);
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_good));
            tvDetailAqiLevel.setText(getString(R.string.aqi_good));
            tvDetailAqiDes.setText(getString(R.string.aqi_good_des));
        } else if (aqi <= 100) {
            aqiSummary = getString(R.string.r_aqi_moderate_des);
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_moderate));
            tvDetailAqiLevel.setText(getString(R.string.aqi_moderate));
            tvDetailAqiDes.setText(getString(R.string.aqi_moderate_des));
        } else if (aqi <= 150) {
            aqiSummary = getString(R.string.r_aqi_unhealthy_sensitive_des);
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_unhealthy_sensitive));
            tvDetailAqiLevel.setText(getString(R.string.aqi_unhealthy_sensitive));
            tvDetailAqiDes.setText(getString(R.string.aqi_unhealthy_sensitive_des));
        } else if (aqi <= 200) {
            aqiSummary = getString(R.string.r_aqi_unhealthy_des);
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_unhealthy));
            tvDetailAqiLevel.setText(getString(R.string.aqi_unhealthy));
            tvDetailAqiDes.setText(getString(R.string.aqi_unhealthy_des));
        } else if (aqi <= 300) {
            aqiSummary = getString(R.string.r_aqi_very_unhealthy_des);
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_very_unhealthy));
            tvDetailAqiLevel.setText(getString(R.string.aqi_very_unhealthy));
            tvDetailAqiDes.setText(getString(R.string.aqi_very_unhealthy_des));
        } else if (aqi <= 500) {
            aqiSummary = getString(R.string.r_aqi_hazardous_des);
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_hazardous));
            tvDetailAqiLevel.setText(getString(R.string.aqi_hazardous));
            tvDetailAqiDes.setText(getString(R.string.aqi_hazardous_des));
        }
        tvAqiSummary.setText(aqiSummary);
        airQualityIndexScale.post(new Runnable() {
            public void run() {
                try {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) aqiIndexIndicator.getLayoutParams();
                    float scaleWidth = (float) airQualityIndexScale.getWidth();
                    float leftMargin = aqi / 500 * scaleWidth;
                    params.leftMargin = (int) leftMargin;
                    aqiIndexIndicator.setLayoutParams(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showAqiDetailDialog() {
        Animation popIn = AnimationUtils.loadAnimation(getActivity(),R.anim.pop_in);
        Animation fadeIn = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_in);
        popIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                aqiDetailLayoutBg.setVisibility(View.VISIBLE);
                aqiDetailLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        aqiDetailLayoutBg.startAnimation(fadeIn);
        aqiDetailLayout.startAnimation(popIn);
        aqiDetailShowing = true;
    }
    public void closeAqiDetailDialog(){
        Animation popOut = AnimationUtils.loadAnimation(getActivity(),R.anim.pop_out);
        final Animation fadeOut = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_out);
        popOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                aqiDetailLayoutBg.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                aqiDetailLayoutBg.setVisibility(View.INVISIBLE);
                aqiDetailLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        aqiDetailLayout.startAnimation(popOut);
        aqiDetailShowing = false;
    }
}
