package com.OdiousPanda.thefweather.Activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.OdiousPanda.thefweather.Adapters.SectionsPagerAdapter;
import com.OdiousPanda.thefweather.CustomUI.MovableFAB;
import com.OdiousPanda.thefweather.DataModel.AQI.AirQuality;
import com.OdiousPanda.thefweather.DataModel.SavedCoordinate;
import com.OdiousPanda.thefweather.DataModel.Weather.Weather;
import com.OdiousPanda.thefweather.MainFragments.DetailsFragment;
import com.OdiousPanda.thefweather.MainFragments.HomeScreenFragment;
import com.OdiousPanda.thefweather.MainFragments.SettingFragment;
import com.OdiousPanda.thefweather.NormalWidget;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Repositories.WeatherRepository;
import com.OdiousPanda.thefweather.Utilities.MyColorUtil;
import com.OdiousPanda.thefweather.ViewModels.WeatherViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;
import com.tooltip.Tooltip;

import java.util.List;
import java.util.Locale;

import mumayank.com.airlocationlibrary.AirLocation;

public class MainActivity extends AppCompatActivity implements HomeScreenFragment.OnLayoutRefreshListener {

    private static final String TAG = "weatherA";
    Tooltip fabTooltip;
    private WeatherViewModel weatherViewModel;
    private boolean firstTimeObserve = true;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout loadingLayout;
    private RelativeLayout noConnectionLayout;
    private ConstraintLayout locationListLayout;
    private MovableFAB fab;
    private ImageView locationListBackButton;
    private ImageView loadingIcon;
    private ImageView addLocation;
    private SlideUp slideUp;
    private boolean locationListShowing = false;
    private boolean screenInitialized = false;
    BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isConnected(context)) {
                if (screenInitialized) {
                    startGettingData();
                }
            } else {
                showNoConnection();
            }
        }
    };
    private boolean toolTipShown = false;
    private int currentBackgroundColor = Color.argb(255, 255, 255, 255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        initViews();
        HomeScreenFragment.getInstance().setOnRefreshListener(this);
        if (isConnected(this)) {
            startGettingData();
        } else {
            showNoConnection();
        }
        Log.d(TAG, "onCreate: ");

        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(NormalWidget.ACTION_TO_DETAILS)) {
                mViewPager.setCurrentItem(2);
            }
        }

    }

    private void showNoConnection() {
        loadingLayout.setVisibility(View.VISIBLE);
        noConnectionLayout.setVisibility(View.VISIBLE);
        fab.hide();
    }

    private void startGettingData() {
        loadingLayout.setVisibility(View.VISIBLE);
        noConnectionLayout.setVisibility(View.INVISIBLE);
        fab.hide();
        setupLocationObservers();
        updateCurrentLocation();
    }

    private void initViews() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        coordinatorLayout = findViewById(R.id.main_content);
        loadingIcon = findViewById(R.id.loading_icon);
        Animation spin = AnimationUtils.loadAnimation(MainActivity.this, R.anim.spin);
        loadingIcon.startAnimation(spin);
        loadingLayout = findViewById(R.id.loading_layout);
        fab = findViewById(R.id.fab);
        fab.hide();
        addLocation = findViewById(R.id.btn_add_location);
        locationListBackButton = findViewById(R.id.btn_go_back);
        noConnectionLayout = findViewById(R.id.no_connection_layout);
        locationListLayout = findViewById(R.id.location_list_layout);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        slideUp = new SlideUpBuilder(locationListLayout)
                .withStartState(SlideUp.State.HIDDEN)
                .withStartGravity(Gravity.BOTTOM)
                .withSlideFromOtherView(coordinatorLayout)
                .build();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationList();
            }
        });
        locationListBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLocationList();
            }
        });
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Thanks for touching me!", Snackbar.LENGTH_SHORT).show();
            }
        });
        fabTooltip = new Tooltip.Builder(fab)
                .setText("Drag me to heart's content")
                .setTextColor(Color.WHITE)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.bg_screen1))
                .setCornerRadius(10f)
                .setTextSize(R.dimen.text_view_14sp)
                .setGravity(Gravity.TOP)
                .setDismissOnClick(true)
                .setCancelable(true)
                .setTypeface(ResourcesCompat.getFont(this, R.font.nunito)).build();
        screenInitialized = true;
    }

    private void showFabToolTips() {
        if (!toolTipShown) {
            fabTooltip.show();
            toolTipShown = true;
        }
    }

    private void setupLocationObservers() {
        weatherViewModel.getAllSavedCoordinate().observe(this, new Observer<List<SavedCoordinate>>() {
            @Override
            public void onChanged(List<SavedCoordinate> savedCoordinates) {
                if (firstTimeObserve) {
                    weatherViewModel.fetchWeather();
                    firstTimeObserve = false;
                }
                setupDataObserver();
            }
        });
    }

    private void setupDataObserver() {
        weatherViewModel.getWeatherData().observe(this, new Observer<List<Weather>>() {
            @Override
            public void onChanged(List<Weather> weathers) {
                DetailsFragment.getInstance().updateData(weathers.get(0));
                HomeScreenFragment.getInstance().updateData(weathers.get(0));
                updateColor();
                loadingLayout.setVisibility(View.INVISIBLE);
                if (mViewPager.getCurrentItem() == 1) {
                    fab.show();
                    showFabToolTips();
                }
            }
        });
        weatherViewModel.getAqiData().observe(this, new Observer<List<AirQuality>>() {
            @Override
            public void onChanged(List<AirQuality> airQualities) {
                DetailsFragment.getInstance().updateAqi(airQualities.get(0));
            }
        });

    }

    private void updateCurrentLocation() {
        Log.d(TAG, "updateCurrentLocation: update Current Location");
        // default ID for current Location
        new AirLocation(this, false, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NonNull Location location) {
                SavedCoordinate currentLocation = new SavedCoordinate(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), null);
                currentLocation.setId(1); // default ID for current Location
                Log.d(TAG, "onSuccess: new coordinate recorded, update db now");
                try {
                    Geocoder geo = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (!addresses.isEmpty()) {
                        String address = addresses.get(0).getAddressLine(0);
                        String[] addressPieces = address.split(",");
                        String locationName;
                        if (addressPieces.length >= 3) {
                            locationName = addressPieces[addressPieces.length - 3].trim();
                        } else {
                            locationName = addressPieces[addressPieces.length - 2].trim();
                        }
                        DetailsFragment.getInstance().updateCurrentLocationName(locationName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                weatherViewModel.update(currentLocation);
            }

            @Override
            public void onFailed(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {

            }
        });
    }

    private void updateViewsWithData() {
        Log.d(TAG, "updateViewsWithData: now updating");
        WeatherRepository.getInstance(this).getWeather();
        WeatherRepository.getInstance(this).getAirQuality();
    }

    @Override
    public void updateData() {
        updateViewsWithData();
    }

    private void updateColor() {
        final int[] argb = MyColorUtil.randomColorCode();
        int textColorCode = MyColorUtil.blackOrWhiteOf(argb);
        ObjectAnimator colorFade = ObjectAnimator.ofObject(coordinatorLayout
                , "backgroundColor"
                , new ArgbEvaluator()
                , currentBackgroundColor
                , Color.argb(argb[0], argb[1], argb[2], argb[3]));
        colorFade.setDuration(200);
        colorFade.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentBackgroundColor = Color.argb(argb[0], argb[1], argb[2], argb[3]);
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

    private void hideLocationList() {
        slideUp.hide();
        if (mViewPager.getCurrentItem() == 1) {
            fab.show();
        }
        locationListShowing = false;
    }

    private void showLocationList() {
        slideUp.show();
        fab.hide();
        locationListShowing = true;
    }

    private boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
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
        if (locationListShowing) {
            hideLocationList();
            return;
        }
        super.onBackPressed();
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
