package com.OdiousPanda.rainbow.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.OdiousPanda.rainbow.API.Constant;
import com.OdiousPanda.rainbow.API.RetrofitService;
import com.OdiousPanda.rainbow.Adapters.LocationListAdapter;
import com.OdiousPanda.rainbow.Adapters.SectionsPagerAdapter;
import com.OdiousPanda.rainbow.CustomUI.MovableFAB;
import com.OdiousPanda.rainbow.DataModel.AQI.AirQuality;
import com.OdiousPanda.rainbow.DataModel.Coordinate;
import com.OdiousPanda.rainbow.DataModel.LocationData;
import com.OdiousPanda.rainbow.DataModel.Unsplash.Unsplash;
import com.OdiousPanda.rainbow.DataModel.Weather.Weather;
import com.OdiousPanda.rainbow.Helpers.SwipeToDeleteCallback;
import com.OdiousPanda.rainbow.MainFragments.DetailsFragment;
import com.OdiousPanda.rainbow.MainFragments.HomeScreenFragment;
import com.OdiousPanda.rainbow.MainFragments.SettingFragment;
import com.OdiousPanda.rainbow.Utilities.PreferencesUtil;
import com.OdiousPanda.rainbow.R;
import com.OdiousPanda.rainbow.Repositories.WeatherRepository;
import com.OdiousPanda.rainbow.Utilities.MyColorUtil;
import com.OdiousPanda.rainbow.ViewModels.WeatherViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import mumayank.com.airlocationlibrary.AirLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements HomeScreenFragment.OnLayoutRefreshListener, LocationListAdapter.OnItemClickListener {

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
    private String currentLocationName = ""; // default ID for current location

    private int AUTOCOMPLETE_REQUEST_CODE = 1201;
    private PlacesClient placesClient;

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
            if (screenInitialized) {
                locationListAdapter = new LocationListAdapter(MainActivity.this, locations, MainActivity.this);
                rvLocations.setAdapter(locationListAdapter);
            }
        }
    };

    BroadcastReceiver backgroundUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (screenInitialized) {
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
        Places.initialize(getApplicationContext(), Constant.GOOGLE_API_KEY);
        placesClient = Places.createClient(this);
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
                        if (percent == 100 && loadingLayout.getVisibility() == View.INVISIBLE && noConnectionLayout.getVisibility() == View.INVISIBLE) {
                            fab.show();
                            locationListShowing = false;
                        } else if (percent < 100) {
                            locationListShowing = true;
                            fab.hide();
                        } else {
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
                openLocationSearch();
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        rvLocations.setLayoutManager(layoutManager);
        locationListAdapter = new LocationListAdapter(MainActivity.this, locations, MainActivity.this);
        rvLocations.setAdapter(locationListAdapter);
        new ItemTouchHelper(new SwipeToDeleteCallback(locationListAdapter)).attachToRecyclerView(rvLocations);

        screenInitialized = true;
    }

    private void openLocationSearch() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setTypeFilter(TypeFilter.CITIES)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    public void onItemClick(int position) {
        if (currentLocationPosition == position) {
            return;
        }
        Log.d("locationListListener", "onItemClick: " + position);
        currentLocationPosition = position;
        currentLocationName = locations.get(currentLocationPosition).getCoordinate().getName();
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
                if (data.size() > 0) {
                    locations = data;
                }
                //Due to the api responses don't come in order, we have to check if the current showing data is from correct location ID
                if (firstTimeFetchViewModel && locations.get(0).getCoordinate().getId() == 1) {
                    DetailsFragment.getInstance().updateData(locations.get(currentLocationPosition).getWeather());
                    HomeScreenFragment.getInstance().updateCurrentLocationName(locations.get(currentLocationPosition).getCoordinate().getName());
                    HomeScreenFragment.getInstance().updateData(locations.get(currentLocationPosition).getWeather());
                    WeatherRepository.getInstance(MainActivity.this).getAirQualityByCoordinate(locations.get(currentLocationPosition).getCoordinate());
                    updateBackground();
                    firstTimeFetchViewModel = false;
                }
                if (dataRefreshing) {
                    if (data.size() > currentLocationPosition) {
                        //Due to the api responses don't come in order, we have to check if the current showing data is from correct location ID
                        if (locations.get(currentLocationPosition).getCoordinate().getName().equalsIgnoreCase(currentLocationName) || currentLocationPosition == 0) {
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


        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if(task.isSuccessful()){
                        FindCurrentPlaceResponse response = task.getResult();
                        assert response != null;
                        if(response.getPlaceLikelihoods().size() > 0){
                            Place p = response.getPlaceLikelihoods().get(0).getPlace();
                            Coordinate currentLocation = new Coordinate();
                            currentLocation.setName(p.getName());
                            currentLocation.setId(1); // default ID for current Location
                            currentLocation.setLat(String.valueOf(Objects.requireNonNull(p.getLatLng()).latitude));
                            currentLocation.setLon(String.valueOf(p.getLatLng().longitude));
                            weatherViewModel.fetchAirQualityByCoordinate(currentLocation);
                            weatherViewModel.update(currentLocation);
                        }
                    }else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    }
                }
            });
        }
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
                            .transition(DrawableTransitionOptions.withCrossFade(200))
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
        int imageResourceId = getResources().getIdentifier("drawable/" + iconName + "_picture", null,getPackageName());
        Glide.with(MainActivity.this).load(imageResourceId)
                .placeholder(background.getDrawable())
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == AUTOCOMPLETE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latLng = place.getLatLng();
                final Coordinate c = new Coordinate();
                assert latLng != null;
                c.setLat(String.valueOf(latLng.latitude));
                c.setLon(String.valueOf(latLng.longitude));
                c.setName(place.getName());
                weatherViewModel.insert(c);
                RetrofitService.createWeatherCall().getWeather(String.valueOf(latLng.latitude),String.valueOf(latLng.longitude))
                        .enqueue(new Callback<Weather>() {
                            @Override
                            public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {
                                if(response.isSuccessful()){
                                    LocationData l = new LocationData(c);
                                    l.setWeather(response.body());
                                    locations.add(l);
                                    locationListAdapter.updateLocationsData(locations);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Weather> call, @NonNull Throwable t) {

                            }
                        });
            } else if(resultCode == AutocompleteActivity.RESULT_ERROR) {
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d(TAG, status.getStatusMessage());
            }
        }
    }
}
