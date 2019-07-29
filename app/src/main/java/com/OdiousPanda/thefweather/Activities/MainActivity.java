package com.OdiousPanda.thefweather.Activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.OdiousPanda.thefweather.API.RetrofitService;
import com.OdiousPanda.thefweather.Adapters.LocationListAdapter;
import com.OdiousPanda.thefweather.Adapters.SectionsPagerAdapter;
import com.OdiousPanda.thefweather.CustomUI.MovableFAB;
import com.OdiousPanda.thefweather.DataModel.AQI.AirQuality;
import com.OdiousPanda.thefweather.DataModel.Coordinate;
import com.OdiousPanda.thefweather.DataModel.LocationData;
import com.OdiousPanda.thefweather.DataModel.Unsplash.Unsplash;
import com.OdiousPanda.thefweather.Helpers.SwipeToDeleteCallback;
import com.OdiousPanda.thefweather.MainFragments.DetailsFragment;
import com.OdiousPanda.thefweather.MainFragments.HomeScreenFragment;
import com.OdiousPanda.thefweather.MainFragments.SettingFragment;
import com.OdiousPanda.thefweather.Utilities.PreferencesUtil;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Repositories.WeatherRepository;
import com.OdiousPanda.thefweather.Utilities.MyColorUtil;
import com.OdiousPanda.thefweather.ViewModels.WeatherViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mumayank.com.airlocationlibrary.AirLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements HomeScreenFragment.OnLayoutRefreshListener,LocationListAdapter.OnItemClickListener {

    private static final String TAG = "weatherA";
    private Tooltip fabTooltip;
    private ImageView background;
    private WeatherViewModel weatherViewModel;
    private boolean firstTimeObserve = true;
    private boolean firstTimeFetchViewModel = true;
    private boolean dataRefreshing = false;
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
    private RecyclerView rvLocations;
    private SlideUp slideUp;
    private boolean locationListShowing = false;
    private boolean screenInitialized = false;
    private LocationListAdapter locationListAdapter;
    private List<LocationData> locations = new ArrayList<>();
    private int currentLocationPosition = 0; // first position of the location list
    private int currentLocationID = 1; // default ID for current location
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

    BroadcastReceiver unitUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: action: " + intent.getAction());
            if(screenInitialized){
                locationListAdapter = new LocationListAdapter(MainActivity.this,locations,MainActivity.this);
                rvLocations.setAdapter(locationListAdapter);
            }
        }
    };

    BroadcastReceiver backgroundUpdateReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(screenInitialized){
                updateBackground();
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

    }

    private void showNoConnection() {
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
        background = findViewById(R.id.background);
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
        rvLocations = findViewById(R.id.locations_rv);
        rvLocations.setHasFixedSize(true);
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
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                        Log.d(TAG, "onSlide: " + percent);
                        if(percent == 100 && loadingLayout.getVisibility() == View.INVISIBLE && noConnectionLayout.getVisibility() == View.INVISIBLE){
                            fab.show();
                            locationListShowing = false;
                        }
                        else if(percent < 100){
                            locationListShowing = true;
                            fab.hide();
                        }
                        else {
                            fab.hide();
                        }
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {

                    }
                })
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
                Snackbar.make(v, "This feature is still under development.", Snackbar.LENGTH_SHORT).show();
            }
        });
        fabTooltip = new Tooltip.Builder(fab)
                .setText("Drag me to your heart's content")
                .setTextColor(Color.WHITE)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.bg_screen1))
                .setCornerRadius(10f)
                .setTextSize(R.dimen.text_view_14sp)
                .setGravity(Gravity.TOP)
                .setDismissOnClick(true)
                .setCancelable(true)
                .setTypeface(ResourcesCompat.getFont(this, R.font.montserrat))
                .build();

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this,RecyclerView.VERTICAL, false);
        rvLocations.setLayoutManager(layoutManager);
        locationListAdapter = new LocationListAdapter(MainActivity.this,locations,MainActivity.this);
        rvLocations.setAdapter(locationListAdapter);
        new ItemTouchHelper(new SwipeToDeleteCallback(locationListAdapter)).attachToRecyclerView(rvLocations);
        screenInitialized = true;
    }

    @Override
    public void onItemClick(int position) {
        if(currentLocationPosition == position){
            return;
        }
        Log.d("locationListListener", "onItemClick: " + position);
        currentLocationPosition = position;
        currentLocationID = locations.get(currentLocationPosition).getCoordinate().getId();
        WeatherRepository.getInstance(MainActivity.this)
                .getAirQualityByCoordinate(locations.get(currentLocationPosition).getCoordinate());
        HomeScreenFragment.getInstance().updateData(locations.get(currentLocationPosition).getWeather());
        DetailsFragment.getInstance().updateData(locations.get(currentLocationPosition).getWeather());
        HomeScreenFragment.getInstance().updateCurrentLocationName(locations.get(currentLocationPosition).getCoordinate().getName());
        updateBackground();
        hideLocationList();
    }

    private void showFabToolTips() {
        if (!toolTipShown) {
            fabTooltip.show();
            toolTipShown = true;
        }
    }

    private void setupLocationObservers() {
        weatherViewModel.getAllSavedCoordinate().observe(this, new Observer<List<Coordinate>>() {
            @Override
            public void onChanged(List<Coordinate> coordinates) {
                if (firstTimeObserve) {
                    weatherViewModel.fetchWeather();

                    firstTimeObserve = false;
                }
                setupDataObserver();
            }
        });
    }

    private void setupDataObserver() {
        weatherViewModel.getAirQualityByCoordinate().observe(MainActivity.this, new Observer<AirQuality>() {
            @Override
            public void onChanged(AirQuality airQuality) {
                DetailsFragment.getInstance().updateAqi(airQuality);
            }
        });
        weatherViewModel.getLocationData().observe(this, new Observer<List<LocationData>>() {
            @Override
            public void onChanged(List<LocationData> data) {
                if(data.size() > 0){
                    locations = data;
                }

                for(LocationData l: locations){
                    l.setCoordinate(updateCoordinateName(l.getCoordinate()));
                }
                    //Due to the api responses don't come in order, we have to check if the current showing data is from correct location ID
                if(firstTimeFetchViewModel && locations.get(0).getCoordinate().getId() == 1){
                    DetailsFragment.getInstance().updateData(locations.get(currentLocationPosition).getWeather());
                    HomeScreenFragment.getInstance().updateCurrentLocationName(locations.get(currentLocationPosition).getCoordinate().getName());
                    HomeScreenFragment.getInstance().updateData(locations.get(currentLocationPosition).getWeather());
                    WeatherRepository.getInstance(MainActivity.this).getAirQualityByCoordinate(locations.get(currentLocationPosition).getCoordinate());
                    updateBackground();
                    firstTimeFetchViewModel = false;
                }
                if(dataRefreshing){
                    if(data.size() > currentLocationPosition){
                        //Due to the api responses don't come in order, we have to check if the current showing data is from correct location ID
                        if(locations.get(currentLocationPosition).getCoordinate().getId() == currentLocationID){
                            DetailsFragment.getInstance().updateData(locations.get(currentLocationPosition).getWeather());
                            HomeScreenFragment.getInstance().updateCurrentLocationName(locations.get(currentLocationPosition).getCoordinate().getName());
                            HomeScreenFragment.getInstance().updateData(locations.get(currentLocationPosition).getWeather());
                            WeatherRepository.getInstance(MainActivity.this).getAirQualityByCoordinate(locations.get(currentLocationPosition).getCoordinate());
                            updateBackground();
                            dataRefreshing = false;
                        }
                    }
                }
                locationListAdapter.updateLocationsData(locations);
                loadingLayout.setVisibility(View.INVISIBLE);
                if (mViewPager.getCurrentItem() == 1 && !locationListShowing) {
                    fab.show();
                    showFabToolTips();
                }

            }
        });
    }

    private void updateCurrentLocation() {
        Log.d(TAG, "updateCurrentLocation: update Current Location");
        // default ID for current Location
        new AirLocation(this, false, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NonNull Location location) {
                Coordinate currentLocation = updateCoordinateName(new Coordinate(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())));
                weatherViewModel.fetchAirQualityByCoordinate(currentLocation);
                currentLocation.setId(1); // default ID for current Location
                Log.d(TAG, "onSuccess: new coordinate recorded, update db now");
                weatherViewModel.update(currentLocation);
            }

            @Override
            public void onFailed(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {

            }
        });
    }

    private Coordinate updateCoordinateName(Coordinate coordinate){
        try {
            Geocoder geo = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(Double.parseDouble(coordinate.getLat()), Double.parseDouble(coordinate.getLon()), 1);
            if (!addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                String[] addressPieces = address.split(",");
                String locationName;
                if (addressPieces.length >= 3) {
                    locationName = addressPieces[addressPieces.length - 3].trim();
                }
                else {
                    int p = addressPieces.length - 2 < 0 ? 0 : addressPieces.length - 2;
                    locationName = addressPieces[p].trim();
                }
                coordinate.setName(locationName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coordinate;
    }

    @Override
    public void updateData() {
        Log.d(TAG, "updateData: now refreshing data");
        dataRefreshing = true;
        WeatherRepository.getInstance(this).getLocationWeathers();
    }

    private void updateBackground() {
        switch (PreferencesUtil.getBackgroundSetting(MainActivity.this)) {
            case PreferencesUtil.BACKGROUND_PICTURE:
                updateBackgroundWeatherPicture();
                break;
            case PreferencesUtil.BACKGROUND_COLOR:
                updateBackgroundColor();
                break;
            case PreferencesUtil.BACKGROUND_PICTURE_RANDOM:
                updateBackgroundRandomPicture();
                break;
        }
    }

    private void updateBackgroundRandomPicture(){
        RetrofitService.createUnsplashCall().getRandomPotrait().enqueue(new Callback<Unsplash>() {
            @Override
            public void onResponse(@NonNull Call<Unsplash> call, @NonNull Response<Unsplash> response) {
                if(response.isSuccessful()){
                    Unsplash unsplash = response.body();
                    assert unsplash != null;
                    Glide.with(MainActivity.this).load(unsplash.urls.regular)
                            .placeholder(background.getDrawable())
                            .transition(DrawableTransitionOptions.withCrossFade(400))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
                                    Palette p = Palette.from(bitmap).generate();
                                    int backgroundColor = p.getDominantColor(Color.BLACK);
                                    int textColor = MyColorUtil.blackOrWhiteOf(backgroundColor);
                                    HomeScreenFragment.getInstance().setColorTheme(textColor);
                                    return false;
                                }
                            })
                            .into(background);
                    HomeScreenFragment.getInstance().updatePhotoDetail(unsplash);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Unsplash> call, @NonNull Throwable t) {

            }
        });
    }

    private void updateBackgroundWeatherPicture(){
        String iconRaw = locations.get(currentLocationPosition).getWeather().getCurrently().getIcon();
        String iconName = iconRaw.replace("-", "_");
        int iconResourceId = getResources().getIdentifier("drawable/" + iconName + "_picture", null,getPackageName());
        Glide.with(MainActivity.this).load(iconResourceId)
                .centerCrop()
                .into(background);
        HomeScreenFragment.getInstance().setColorTheme(Color.WHITE);
    }

    private void updateBackgroundColor(){
        background.setImageResource(android.R.color.transparent);
        final int[] argb = MyColorUtil.randomColorCode();
        int textColorCode = MyColorUtil.blackOrWhiteOf(argb);
        ObjectAnimator colorFade = ObjectAnimator.ofObject(background
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
        loadingLayout.setVisibility(View.INVISIBLE);
        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        registerReceiver(unitUpdateReceiver, new IntentFilter(SettingFragment.ACTION_UPDATE_UNIT));
        registerReceiver(backgroundUpdateReceiver,new IntentFilter(SettingFragment.ACTION_UPDATE_BACKGROUND));
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectionChangeReceiver);
        unregisterReceiver(unitUpdateReceiver);
        unregisterReceiver(backgroundUpdateReceiver);
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onBackPressed() {
        if (locationListShowing) {
            hideLocationList();
            return;
        }
        if(SettingFragment.getInstance().aboutMeShowing){
            SettingFragment.getInstance().closeAboutMeDialog();
            return;
        }
        if(HomeScreenFragment.getInstance().photoDetailsShowing){
            HomeScreenFragment.getInstance().closePhotoDetailBox();
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
        finish();
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
