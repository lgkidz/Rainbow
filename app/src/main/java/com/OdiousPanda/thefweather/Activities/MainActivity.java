package com.OdiousPanda.thefweather.Activities;

import android.content.SharedPreferences;
import android.location.Location;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.OdiousPanda.thefweather.Adapters.SectionsPagerAdapter;
import com.OdiousPanda.thefweather.MainFragments.HomeScreenFragment;
import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;
import com.OdiousPanda.thefweather.Model.Forecast.ForecastWeather;
import com.OdiousPanda.thefweather.Model.SavedCoordinate;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Repositories.WeatherRepository;
import com.OdiousPanda.thefweather.ViewModels.WeatherViewModel;
import java.util.List;
import mumayank.com.airlocationlibrary.AirLocation;


public class MainActivity extends AppCompatActivity implements HomeScreenFragment.OnLayoutRefreshListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    SharedPreferences sharedPreferences;
    private static final String TAG = "weatherA";
    private AirLocation airLocation;
    private boolean firstTimeObserve = true;

    WeatherViewModel weatherViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_key_string),MODE_PRIVATE);
        initViews();
        setupLocationObservers();
        updateCurrentLocation();

    }

    private void setupLocationObservers(){
        weatherViewModel.getAllSavedCoordinate().observe(this, new Observer<List<SavedCoordinate>>() {
            @Override
            public void onChanged(List<SavedCoordinate> savedCoordinates) {
                if(firstTimeObserve){
                    weatherViewModel.fetchCurrentWeather();
                    weatherViewModel.fetchForecastWeather();
                    firstTimeObserve = false;
                }
                setupDataObserver();

            }
        });


    }

    private void setupDataObserver(){
        weatherViewModel.getCurrentWeatherData().observe(this, new Observer<List<CurrentWeather>>() {
            @Override
            public void onChanged(List<CurrentWeather> currentWeathers) {
                //Update CurrentWeather Fragment
                if(currentWeathers.size() == 0){
                    Toast.makeText(MainActivity.this, "Hold on!", Toast.LENGTH_SHORT).show();
                }
                else {
                    HomeScreenFragment.getInstance().updateData(currentWeathers.get(0));
                }

            }
        });

        weatherViewModel.getForecastWeatherData().observe(this, new Observer<List<ForecastWeather>>() {
            @Override
            public void onChanged(List<ForecastWeather> forecastWeathers) {
                //Update Forecast Fragment
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
        WeatherRepository.getInstance(getApplication()).getCurrentWeather();
        WeatherRepository.getInstance(getApplication()).getForecastWeather();
    }

    private void initViews(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        HomeScreenFragment.getInstance().setOnTextClickListener(this);

    }

    @Override
    public void updateData() {
        updateViewsWithData();
    }
}
