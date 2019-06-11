package com.OdiousPanda.thefweather.Activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import com.OdiousPanda.thefweather.Adapters.SectionsPagerAdapter;
import com.OdiousPanda.thefweather.MainFragments.HomeScreenFragment;
import com.OdiousPanda.thefweather.MainFragments.SettingFragment;
import com.OdiousPanda.thefweather.Model.SavedCoordinate;
import com.OdiousPanda.thefweather.Model.Weather.Weather;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Repositories.WeatherRepository;
import com.OdiousPanda.thefweather.Utilities.MyColorUtil;
import com.OdiousPanda.thefweather.ViewModels.WeatherViewModel;
import java.util.List;
import mumayank.com.airlocationlibrary.AirLocation;


public class MainActivity extends AppCompatActivity implements HomeScreenFragment.OnLayoutRefreshListener{

    private static final String TAG = "weatherA";

    private WeatherViewModel weatherViewModel;
    private boolean firstTimeObserve = true;
    private AirLocation airLocation;
    SharedPreferences sharedPreferences;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private CoordinatorLayout coordinatorLayout;

    private int currentBackgroundColor = Color.argb(255,255,255,255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_key_string),MODE_PRIVATE);
        initViews();
        setupLocationObservers();
        updateCurrentLocation();
        HomeScreenFragment.getInstance().setOnRefreshListener(this);

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    private void initViews(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        coordinatorLayout = findViewById(R.id.main_content);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
    }

    private void setupLocationObservers(){
        weatherViewModel.getAllSavedCoordinate().observe(this, new Observer<List<SavedCoordinate>>() {
            @Override
            public void onChanged(List<SavedCoordinate> savedCoordinates) {
                if(firstTimeObserve){
                    weatherViewModel.fetchWeather();
                    firstTimeObserve = false;
                }
                setupDataObserver();
            }
        });
    }

    private void setupDataObserver(){
        weatherViewModel.getWeatherData().observe(this, new Observer<List<Weather>>() {
            @Override
            public void onChanged(List<Weather> weathers) {
                HomeScreenFragment.getInstance().updateData(weathers.get(0));
                updateColor();
            }
        });

    }

    private void updateCurrentLocation(){
        Log.d(TAG, "updateCurrentLocation: update Current Location");
        airLocation = new AirLocation(this, false, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(Location location) {
                SavedCoordinate currentLocation = new SavedCoordinate(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()), null);
                currentLocation.setId(1); // default ID for current Location
                Log.d(TAG, "onSuccess: new coordinate recorded, update db now");
                weatherViewModel.update(currentLocation);
            }

            @Override
            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {

            }
        });
    }

    private void updateViewsWithData(){
        Log.d(TAG, "updateViewsWithData: now updating");
        WeatherRepository.getInstance(getApplication()).getWeather();
    }

    @Override
    public void updateData() {
        updateViewsWithData();
    }

    private void updateColor(){
        final int[] argb = MyColorUtil.randomColorCode();
        int textColorCode = MyColorUtil.blackOrWhiteOf(argb);
        ObjectAnimator colorFade = ObjectAnimator.ofObject(coordinatorLayout
                ,"backgroundColor"
                , new ArgbEvaluator()
                , currentBackgroundColor
                , Color.argb(argb[0],argb[1],argb[2],argb[3]));
        colorFade.setDuration(200);
        colorFade.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentBackgroundColor = Color.argb(argb[0],argb[1],argb[2],argb[3]);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        colorFade.start();
        HomeScreenFragment.getInstance().setColorTheme(textColorCode);
        SettingFragment.getInstance().updateColorTheme(argb);
    }
}
