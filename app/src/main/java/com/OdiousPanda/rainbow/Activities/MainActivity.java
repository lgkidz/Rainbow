package com.OdiousPanda.rainbow.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.OdiousPanda.rainbow.DataModel.Nearby.NearbySearch;
import com.OdiousPanda.rainbow.DataModel.Unsplash.Unsplash;
import com.OdiousPanda.rainbow.DataModel.Weather.Weather;
import com.OdiousPanda.rainbow.Helpers.SwipeToDeleteCallback;
import com.OdiousPanda.rainbow.MainFragments.DetailsFragment;
import com.OdiousPanda.rainbow.MainFragments.HomeScreenFragment;
import com.OdiousPanda.rainbow.MainFragments.SettingFragment;
import com.OdiousPanda.rainbow.R;
import com.OdiousPanda.rainbow.Repositories.WeatherRepository;
import com.OdiousPanda.rainbow.Utilities.ColorUtil;
import com.OdiousPanda.rainbow.Utilities.NotificationUtil;
import com.OdiousPanda.rainbow.Utilities.PreferencesUtil;
import com.OdiousPanda.rainbow.Utilities.TextUtil;
import com.OdiousPanda.rainbow.ViewModels.WeatherViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import mumayank.com.airlocationlibrary.AirLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements HomeScreenFragment.OnLayoutRefreshListener, LocationListAdapter.OnItemClickListener {

    private static final String TAG = "weatherA";
    private ImageView background;
    private WeatherViewModel weatherViewModel;
    private boolean firstTimeObserve = true;
    private boolean firstTimeFetchViewModel = true;
    private boolean dataRefreshing = false;
    private ViewPager mViewPager;
    private RelativeLayout loadingLayout;
    private ConstraintLayout noConnectionLayout;
    private MovableFAB fab;
    BroadcastReceiver shareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            takeScreenShot();
        }
    };
    private RecyclerView rvLocations;
    private SlideUp slideUp;
    private boolean locationListShowing = false;
    private boolean screenInitialized = false;
    private LocationListAdapter locationListAdapter;
    private List<LocationData> locations = new ArrayList<>();
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
    private int currentLocationPosition = 0; // first position of the location list
    private String currentLocationName = ""; // default ID for current location
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
    private int AUTOCOMPLETE_REQUEST_CODE = 1201;
    private int currentBackgroundColor = Color.argb(255, 255, 255, 255);
    BroadcastReceiver backgroundUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (screenInitialized) {
                updateBackground();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Places.initialize(getApplicationContext(), Constant.GOOGLE_API_KEY);
        PlacesClient placesClient = Places.createClient(this);

        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        initViews();
        HomeScreenFragment.getInstance().setOnRefreshListener(this);
        if (isConnected(this)) {
            startGettingData();
        } else {
            showNoConnection();
        }
        Log.d(TAG, "onCreate: ");
        if (PreferencesUtil.getNotificationSetting(this).equals(PreferencesUtil.NOTIFICATION_SETTING_ON)) {
            NotificationUtil notificationUtil = new NotificationUtil(this);
            notificationUtil.startDailyNotification();
        }

        PreferencesUtil.increaseAppOpenCount(this);
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
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        background = findViewById(R.id.background);
        mViewPager = findViewById(R.id.container);
        CoordinatorLayout coordinatorLayout = findViewById(R.id.main_content);
        ImageView loadingIcon = findViewById(R.id.loading_icon);
        Animation spin = AnimationUtils.loadAnimation(MainActivity.this, R.anim.spin);
        loadingIcon.startAnimation(spin);
        loadingLayout = findViewById(R.id.loading_layout);
        fab = findViewById(R.id.fab);
        fab.hide();
        ImageView addLocation = findViewById(R.id.btn_add_location);
        ImageView locationListBackButton = findViewById(R.id.btn_go_back);
        rvLocations = findViewById(R.id.locations_rv);
        rvLocations.setHasFixedSize(true);
        noConnectionLayout = findViewById(R.id.no_connection_layout);
        ConstraintLayout locationListLayout = findViewById(R.id.location_list_layout);
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
                .setTypeFilter(TypeFilter.REGIONS)
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
        searchNearbyPlaceToEat(locations.get(currentLocationPosition).getCoordinate().getLat(), locations.get(currentLocationPosition).getCoordinate().getLon());
        updateBackground();
        hideLocationList();
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
                            if (locations.get(currentLocationPosition).getCoordinate().getName().equalsIgnoreCase(currentLocationName) || locations.get(currentLocationPosition).getCoordinate().getId() == 1) {
                                DetailsFragment.getInstance().updateData(locations.get(currentLocationPosition).getWeather());
                                HomeScreenFragment.getInstance().updateCurrentLocationName(locations.get(currentLocationPosition).getCoordinate().getName());
                                HomeScreenFragment.getInstance().updateData(locations.get(currentLocationPosition).getWeather());
                                WeatherRepository.getInstance(MainActivity.this).getAirQualityByCoordinate(locations.get(currentLocationPosition).getCoordinate());
                                updateBackground();
                                dataRefreshing = false;
                            }
                        }
                    }
                }
//                Due to the api responses don't come in order, we have to check if the current showing data is from correct location ID

                locationListAdapter.updateLocationsData(locations);
                loadingLayout.setVisibility(View.INVISIBLE);
                if (mViewPager.getCurrentItem() == 1 && !locationListShowing) {
                    fab.show();
                }

            }
        });
    }

    private void searchNearbyPlaceToEat(String lat, String lon) {
        String locationString = TextUtil.locationStringForNearbySearch(lat, lon);
        int radius = Constant.NEARBY_SEARCH_RADIUS_DEFAULT;
        String keyword = Constant.NEARBY_SEARCH_KEYWORD;
        String apiKey = Constant.GOOGLE_API_KEY;
        RetrofitService.createNearbySearchCall().searchNearby(locationString, radius, keyword, apiKey).enqueue(new Callback<NearbySearch>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearch> call, @NonNull Response<NearbySearch> response) {
                if (response.isSuccessful()) {
                    NearbySearch nearbySearch = response.body();
                    DetailsFragment.getInstance().updateNearbySearchData(nearbySearch);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbySearch> call, @NonNull Throwable t) {

            }
        });
    }

    private void updateCurrentLocation() {
        Log.d(TAG, "updateCurrentLocation: update Current Location");
        AirLocation airLocation = new AirLocation(this, false, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NonNull Location location) {
                Coordinate currentLocation = new Coordinate(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
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
                        currentLocation.setName(locationName);
                        HomeScreenFragment.getInstance().updateCurrentLocationName(locationName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                weatherViewModel.update(currentLocation);
                weatherViewModel.fetchAirQualityByCoordinate(currentLocation);
                searchNearbyPlaceToEat(currentLocation.getLat(), currentLocation.getLon());
            }

            @Override
            public void onFailed(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {

            }
        });
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

    private void updateBackgroundRandomPicture() {
        RetrofitService.createUnsplashCall().getRandomPotrait().enqueue(new Callback<Unsplash>() {
            @Override
            public void onResponse(@NonNull Call<Unsplash> call, @NonNull Response<Unsplash> response) {
                if (response.isSuccessful()) {
                    Unsplash unsplash = response.body();
                    assert unsplash != null;
                    Glide.with(MainActivity.this).load(unsplash.urls.regular)
                            .placeholder(background.getDrawable())
                            .transition(DrawableTransitionOptions.withCrossFade(200))
                            .centerCrop()
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                    Palette p = Palette.from(bitmap).generate();
                                    int backgroundColor = p.getDominantColor(Color.BLACK);
                                    int textColor = ColorUtil.blackOrWhiteOf(backgroundColor);
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

    private void updateBackgroundWeatherPicture() {
        String iconRaw = locations.get(currentLocationPosition).getWeather().getCurrently().getIcon();
        String iconName = iconRaw.replace("-", "_");
        int imageResourceId = getResources().getIdentifier("drawable/" + iconName + "_picture", null, getPackageName());
        Glide.with(MainActivity.this).load(imageResourceId)
                .placeholder(background.getDrawable())
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .centerCrop()
                .into(background);
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(imageResourceId)).getBitmap();
        Palette p = Palette.from(bitmap).generate();
        int backgroundColor = p.getDominantColor(Color.BLACK);
        int textColor = ColorUtil.blackOrWhiteOf(backgroundColor);
        HomeScreenFragment.getInstance().setColorTheme(textColor);
    }

    private void updateBackgroundColor() {
        background.setImageResource(android.R.color.transparent);
        final int[] argb = ColorUtil.randomColorCode();
        int textColorCode = ColorUtil.blackOrWhiteOf(argb);
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

    @SuppressLint("RestrictedApi")
    private void share() {
        Log.d(TAG, "takeScreenShot: ");
        HomeScreenFragment.getInstance().hideShareIcon();
        fab.setVisibility(View.INVISIBLE);
        long now = System.currentTimeMillis();
        File directory = new File(Environment.getExternalStorageDirectory().toString() + "/Rainbow/");
        directory.mkdirs();
        try {
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(directory, now + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_this_to)));
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }

        HomeScreenFragment.getInstance().showShareIcon();
        fab.setVisibility(View.VISIBLE);
    }

    private void takeScreenShot() {
        Log.d(TAG, "takeScreenShot: ");
        Dexter.withActivity(MainActivity.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingDialog();
                        } else {
                            if (!report.areAllPermissionsGranted()) {
                                showNeededPermissionDialog();
                            } else {
                                share();
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).onSameThread().check();
    }

    private void showNeededPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.message_need_permission));
        builder.setMessage(getString(R.string.rainbow_need_permission));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                takeScreenShot();
            }
        });
        builder.show();
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.message_need_permission));
        builder.setMessage(getString(R.string.message_grant_permission));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.label_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingLayout.setVisibility(View.INVISIBLE);
        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        registerReceiver(unitUpdateReceiver, new IntentFilter(SettingFragment.ACTION_UPDATE_UNIT));
        registerReceiver(backgroundUpdateReceiver, new IntentFilter(SettingFragment.ACTION_UPDATE_BACKGROUND));
        registerReceiver(shareReceiver, new IntentFilter(HomeScreenFragment.ACTION_SHARE_RAINBOW));
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectionChangeReceiver);
        unregisterReceiver(unitUpdateReceiver);
        unregisterReceiver(backgroundUpdateReceiver);
        unregisterReceiver(shareReceiver);
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onBackPressed() {
        if (locationListShowing) {
            hideLocationList();
            return;
        }
        if (SettingFragment.getInstance().aboutMeShowing) {
            SettingFragment.getInstance().closeAboutMeDialog();
            return;
        }
        if (DetailsFragment.getInstance().aqiMoreDeatailsShowing) {
            DetailsFragment.getInstance().closeAqiMoreDetailsDialog();
            return;
        }
        if (HomeScreenFragment.getInstance().photoDetailsShowing) {
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
        //finish();
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
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latLng = place.getLatLng();
                final Coordinate c = new Coordinate();
                assert latLng != null;
                c.setLat(String.valueOf(latLng.latitude));
                c.setLon(String.valueOf(latLng.longitude));
                c.setName(place.getName());
                weatherViewModel.insert(c);
                RetrofitService.createWeatherCall().getWeather(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude))
                        .enqueue(new Callback<Weather>() {
                            @Override
                            public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {
                                if (response.isSuccessful()) {
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
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d(TAG, status.getStatusMessage());
            }
        }
    }
}
