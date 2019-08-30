package com.OdiousPanda.rainbow.MainFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OdiousPanda.rainbow.Adapters.HourlyForecastAdapter;
import com.OdiousPanda.rainbow.DataModel.AQI.AirQuality;
import com.OdiousPanda.rainbow.DataModel.Nearby.NearbySearch;
import com.OdiousPanda.rainbow.DataModel.Weather.Weather;
import com.OdiousPanda.rainbow.R;
import com.OdiousPanda.rainbow.Utilities.ClothesIconUtil;
import com.OdiousPanda.rainbow.Utilities.FoodIconUtil;
import com.OdiousPanda.rainbow.Utilities.PreferencesUtil;
import com.OdiousPanda.rainbow.Utilities.UnitConverter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.DateFormat;
import java.util.Objects;
import java.util.Random;

public class DetailsFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static DetailsFragment instance;

    private Weather currentWeather;
    private TextView tvSunTitle;
    private TextView tvRealFeel;
    private TextView tvPressure;
    private TextView tvPressureTitle;
    private TextView tvWindSpeed;
    private TextView tvWindDirection;
    private ImageView windmillWings;
    private LinearLayout airQualityIndexScale;
    private ImageView aqiIndexIndicator;
    private TextView tvDetailAqiIndex;
    private TextView tvDetailAqiLevel;
    private TextView tvDetailAqiDes;
    private TextView tvCloudCover;
    private TextView tvVisibility;

    private ImageView icHead;
    private ImageView icUpper;
    private ImageView icLower;
    private ImageView icFoot;
    private ImageView icHand;
    private TextView tvWearCause;

    private ImageView icFoodType;
    private TextView tvFood;
    private ImageView icRefreshFood;
    private FoodIconUtil foodIconUtil;

    private TextView tvSunrise;
    private TextView tvMidday;
    private TextView tvSunset;
    private RecyclerView hourlyForecastRv;

    private NearbySearch nearbySearchData = new NearbySearch();

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
        foodIconUtil = new FoodIconUtil();
        return v;
    }

    private void initViews(View v) {
        tvSunTitle = v.findViewById(R.id.sun_title);
        tvRealFeel = v.findViewById(R.id.tv_tempRealFeel);
        tvPressure = v.findViewById(R.id.tv_pressure);
        tvPressureTitle = v.findViewById(R.id.tv_pressureTitle);
        tvWindSpeed = v.findViewById(R.id.wind_speed);
        tvWindDirection = v.findViewById(R.id.wind_direction);
        windmillWings = v.findViewById(R.id.windmill_wings);
        tvDetailAqiIndex = v.findViewById(R.id.tv_aqi_index);
        tvDetailAqiLevel = v.findViewById(R.id.tv_aqi_level);
        tvDetailAqiDes = v.findViewById(R.id.tv_aqi_des);
        aqiIndexIndicator = v.findViewById(R.id.aqi_index_indicator);
        airQualityIndexScale = v.findViewById(R.id.index_scale);
        tvCloudCover = v.findViewById(R.id.cloudCover_value);
        tvVisibility = v.findViewById(R.id.tv_visibility);
        tvSunrise = v.findViewById(R.id.sunrise_time);
        tvMidday = v.findViewById(R.id.midDay_time);
        tvSunset = v.findViewById(R.id.sunset_time);

        hourlyForecastRv = v.findViewById(R.id.hourlyForecastRv);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        hourlyForecastRv.setLayoutManager(manager);

        icHead = v.findViewById(R.id.ic_headWear);
        icUpper = v.findViewById(R.id.ic_upperBody);
        icLower = v.findViewById(R.id.ic_lowerBody);
        icFoot = v.findViewById(R.id.ic_footWare);
        icHand = v.findViewById(R.id.ic_hand);
        tvWearCause = v.findViewById(R.id.tvWearCause);

        icFoodType = v.findViewById(R.id.ic_foodType);
        tvFood = v.findViewById(R.id.food);
        icRefreshFood = v.findViewById(R.id.ic_refreshFood);
        icRefreshFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation spin = AnimationUtils.loadAnimation(getActivity(), R.anim.quick_spin);
                icRefreshFood.startAnimation(spin);
                if (nearbySearchData.getResults() != null) {
                    if (nearbySearchData.getResults().size() > 0) {
                        updateFoodData();
                    }
                }
            }
        });
        if (currentWeather != null) {
            updateUnit();
        }

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = v.findViewById(R.id.adView);
        adView.loadAd(adRequest);
    }

    void updateUnit() {
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(Objects.requireNonNull(getActivity()));
        String currentSpeedUnit = PreferencesUtil.getSpeedUnit(getActivity());
        String currentPressureUnit = PreferencesUtil.getPressureUnit(getActivity());
        String currentDistanceUnit = PreferencesUtil.getDistanceUnit(getActivity());
        updateVisibilityData(currentDistanceUnit);
        updateRealFeelTemperature(currentTempUnit);
        updatePressureData(currentPressureUnit);
        updateWindSpeedData(currentSpeedUnit);
        updateHourlyForecastDat();
        updateSunData();
    }

    private void updateSunData() {
        tvSunTitle.setText(PreferencesUtil.isExplicit(Objects.requireNonNull(getActivity())) ? getString(R.string.sun) : getString(R.string.sun_not_explicit));
        DateFormat df = DateFormat.getTimeInstance(java.text.DateFormat.SHORT);
        long sunriseTime = currentWeather.getDaily().getData().get(0).getSunriseTime() * 1000;
        long sunsetTime = currentWeather.getDaily().getData().get(0).getSunsetTime() * 1000;
        long middayTime = (sunriseTime + sunsetTime) / 2;
        tvSunrise.setText(df.format(sunriseTime));
        tvMidday.setText(df.format(middayTime));
        tvSunset.setText(df.format(sunsetTime));
    }

    private void updateVisibilityData(String currentDistanceUnit) {
        tvVisibility.setText(UnitConverter.convertToDistanceUnit(currentWeather.getCurrently().getVisibility(), currentDistanceUnit));
    }

    public void updateData(Weather weather) {
        currentWeather = weather;
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(Objects.requireNonNull(getActivity()));
        String currentSpeedUnit = PreferencesUtil.getSpeedUnit(getActivity());
        String currentPressureUnit = PreferencesUtil.getPressureUnit(getActivity());
        String currentDistanceUnit = PreferencesUtil.getDistanceUnit(getActivity());
        updateVisibilityData(currentDistanceUnit);
        updateWearIcons();
        updateRealFeelTemperature(currentTempUnit);
        updateCloudCoverData();
        updatePressureData(currentPressureUnit);
        updateWindSpeedData(currentSpeedUnit);
        updateSunData();
        updateHourlyForecastDat();
    }

    private void updateHourlyForecastDat() {
        HourlyForecastAdapter adapter = new HourlyForecastAdapter(currentWeather.getHourly().getData(), getActivity());
        hourlyForecastRv.setAdapter(adapter);
    }

    private void updateFoodData() {
        icFoodType.setImageResource(foodIconUtil.getRandomIcons());
        String currentPlaceName = tvFood.getText().toString();
        String placeName;
        while (true) {
            int p = new Random().nextInt(nearbySearchData.getResults().size());
            if (!nearbySearchData.getResults().get(p).getName().equalsIgnoreCase(currentPlaceName)) {
                placeName = nearbySearchData.getResults().get(p).getName();
                break;
            }
        }
        SpannableString content = new SpannableString(placeName);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvFood.setText(content);
        String uri = "geo:0,0?q=" + placeName;
        final Intent toMapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        tvFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(toMapIntent);
            }
        });
    }

    public void updateNearbySearchData(NearbySearch nearbySearch) {
        nearbySearchData = nearbySearch;
        if (nearbySearchData.getResults().size() > 0) {
            updateFoodData();
        }
    }

    private void updateWearIcons() {
        ClothesIconUtil clothesIconUtil = new ClothesIconUtil(currentWeather);
        icHead.setImageResource(clothesIconUtil.getHeadIcon());
        icUpper.setImageResource(clothesIconUtil.getUpperIcon());
        icLower.setImageResource(clothesIconUtil.getLowerIcon());
        icFoot.setImageResource(clothesIconUtil.getFootIcon());
        icHand.setImageResource(clothesIconUtil.getHandIcon());
        String cause = getString(R.string.cause_it_s) + clothesIconUtil.getCause();
        tvWearCause.setText(cause);
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
        boolean isExplicit = PreferencesUtil.isExplicit(Objects.requireNonNull(getActivity()));
        String placeholder = isExplicit ? getString(R.string.real_feel_title_explicit) : getString(R.string.real_feel_title);
        String realfeel = placeholder + " " + UnitConverter.convertToTemperatureUnitClean(currentWeather.getCurrently().getApparentTemperature(), currentTempUnit);
        tvRealFeel.setText(realfeel);
    }

    private void updatePressureData(String currentPressureUnit) {
        tvPressure.setText(UnitConverter.convertToPressureUnit(getActivity(), currentWeather.getCurrently().getPressure(), currentPressureUnit));
        if (currentPressureUnit.equals(getString(R.string.depression_unit))) {
            tvPressureTitle.setText(getString(R.string.depression_level_title));
        } else {
            tvPressureTitle.setText(getString(R.string.pressure_title));
        }
    }

    private void updateCloudCoverData() {
        float cloudPercent = currentWeather.getCurrently().getCloudCover();
        String cloud = Math.round(cloudPercent * 100) + "%";
        tvCloudCover.setText(cloud);
    }

    public void updateAqi(AirQuality air) {
        final float aqi = air.getData().aqi;
        tvDetailAqiIndex.setText(String.valueOf((int) aqi));
        Context context = getActivity();
        assert context != null;
        if (aqi <= 50) {
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context, R.color.aqi_good));
            tvDetailAqiLevel.setText(getString(R.string.aqi_good));
            tvDetailAqiDes.setText(getString(R.string.aqi_good_des));
        } else if (aqi <= 100) {
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context, R.color.aqi_moderate));
            tvDetailAqiLevel.setText(getString(R.string.aqi_moderate));
            tvDetailAqiDes.setText(getString(R.string.aqi_moderate_des));
        } else if (aqi <= 150) {
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context, R.color.aqi_unhealthy_sensitive));
            tvDetailAqiLevel.setText(getString(R.string.aqi_unhealthy_sensitive));
            tvDetailAqiDes.setText(getString(R.string.aqi_unhealthy_sensitive_des));
        } else if (aqi <= 200) {
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context, R.color.aqi_unhealthy));
            tvDetailAqiLevel.setText(getString(R.string.aqi_unhealthy));
            tvDetailAqiDes.setText(getString(R.string.aqi_unhealthy_des));
        } else if (aqi <= 300) {
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context, R.color.aqi_very_unhealthy));
            tvDetailAqiLevel.setText(getString(R.string.aqi_very_unhealthy));
            tvDetailAqiDes.setText(getString(R.string.aqi_very_unhealthy_des));
        } else {
            tvDetailAqiIndex.setTextColor(ContextCompat.getColor(context, R.color.aqi_hazardous));
            tvDetailAqiLevel.setText(getString(R.string.aqi_hazardous));
            tvDetailAqiDes.setText(getString(R.string.aqi_hazardous_des));
        }
        airQualityIndexScale.post(new Runnable() {
            public void run() {
                try {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) aqiIndexIndicator.getLayoutParams();
                    float scaleWidth = (float) airQualityIndexScale.getWidth();
                    float leftMargin = aqi / 500 * scaleWidth;
                    if(aqi > 500){
                        leftMargin = scaleWidth;
                    }
                    params.leftMargin = (int) leftMargin - aqiIndexIndicator.getWidth()/2;

                    aqiIndexIndicator.setLayoutParams(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
