package com.OdiousPanda.thefweather.Activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.OdiousPanda.thefweather.Adapters.SectionsPagerAdapter;
import com.OdiousPanda.thefweather.MainFragments.DetailsFragment;
import com.OdiousPanda.thefweather.MainFragments.HomeScreenFragment;
import com.OdiousPanda.thefweather.MainFragments.SettingFragment;
import com.OdiousPanda.thefweather.Model.AQI.AirQuality;
import com.OdiousPanda.thefweather.Model.SavedCoordinate;
import com.OdiousPanda.thefweather.Model.Weather.Weather;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Repositories.WeatherRepository;
import com.OdiousPanda.thefweather.Utilities.MyColorUtil;
import com.OdiousPanda.thefweather.ViewModels.WeatherViewModel;
import java.util.List;
import mumayank.com.airlocationlibrary.AirLocation;


public class MainActivity extends AppCompatActivity implements HomeScreenFragment.OnLayoutRefreshListener{

    BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isConnected(context)){
                if(screenInitialized){
                    startGettingData();
                }
            }
            else{
                showNoConnection();
            }
        }
    };


    private static final String TAG = "weatherA";

    private WeatherViewModel weatherViewModel;
    private boolean firstTimeObserve = true;
    private AirLocation airLocation;
    SharedPreferences sharedPreferences;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout loadingLayout;
    private RelativeLayout noConnectionLayout;
    private ImageView loadingIcon;
    private boolean screenInitialized = false;

    private int currentBackgroundColor = Color.argb(255,255,255,255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_key_string),MODE_PRIVATE);
        initViews();
        HomeScreenFragment.getInstance().setOnRefreshListener(this);
        if(isConnected(this)){
            startGettingData();
        }
        else{
            showNoConnection();
        }
        Log.d(TAG, "onCreate: ");
    }

    private void showNoConnection(){
        loadingLayout.setVisibility(View.VISIBLE);
        noConnectionLayout.setVisibility(View.VISIBLE);
    }

    private void startGettingData(){
        loadingLayout.setVisibility(View.VISIBLE);
        noConnectionLayout.setVisibility(View.INVISIBLE);
        setupLocationObservers();
        updateCurrentLocation();
    }

    private void initViews(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        coordinatorLayout = findViewById(R.id.main_content);
        loadingIcon = findViewById(R.id.loading_icon);
        Animation spin = AnimationUtils.loadAnimation(MainActivity.this, R.anim.spin);
        loadingIcon.startAnimation(spin);
        loadingLayout = findViewById(R.id.loading_layout);
        noConnectionLayout = findViewById(R.id.no_connection_layout);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(1);
        screenInitialized = true;
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
                DetailsFragment.getInstance().updateData(weathers.get(0));
                HomeScreenFragment.getInstance().updateData(weathers.get(0));
                updateColor();
                loadingLayout.setVisibility(View.INVISIBLE);
            }
        });
        weatherViewModel.getAqiData().observe(this, new Observer<List<AirQuality>>() {
            @Override
            public void onChanged(List<AirQuality> airQualities) {
                DetailsFragment.getInstance().updateAqi(airQualities.get(0));
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
        WeatherRepository.getInstance(getApplication()).getAirQuality();
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
        DetailsFragment.getInstance().updateColorTheme(argb);
        SettingFragment.getInstance().updateColorTheme(argb);
        HomeScreenFragment.getInstance().setColorTheme(textColorCode);
    }

    private boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectionChangeReceiver,new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectionChangeReceiver);
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //moveTaskToBack(true);
        Log.d(TAG, "onBackPressed: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
}
