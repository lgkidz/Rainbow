package com.OdiousPanda.thefweather.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import com.OdiousPanda.thefweather.Adapters.SectionsPagerAdapter;
import com.OdiousPanda.thefweather.MainFragments.ForecastFragment;
import com.OdiousPanda.thefweather.MainFragments.HomeScreenFragment;
import com.OdiousPanda.thefweather.Model.SavedCoord;
import com.OdiousPanda.thefweather.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 2108;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    SharedPreferences sharedPreferences;
    Gson gson;
    public static final String TAG = "location cord";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(getString(R.string.pref_key_string),MODE_PRIVATE);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        gson = new Gson();

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {

            buildLocationRequest();
            buildLocationCallback();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        buildLocationRequest();
                        buildLocationCallback();
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    else if (grantResults[0] == PackageManager.PERMISSION_DENIED){

                    }
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void buildLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);

    }

    private void updateLocation(Location location){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SavedCoord savedCoord1Object = new SavedCoord(location.getLatitude(),location.getLongitude());
        String savedCoordToString = gson.toJson(savedCoord1Object);
        editor.putString(getString(R.string.saved_coord_pref_key),savedCoordToString);
        editor.apply();
        HomeScreenFragment.getInstance().updateLocation(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
        ForecastFragment.getInstance().updateLocation(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location: locationResult.getLocations()){
                    String savedCoordJson = sharedPreferences.getString(getString(R.string.saved_coord_pref_key), null);
                    if(savedCoordJson == null){
                        updateLocation(location);
                    }
                    else{
                        Type type = new TypeToken<SavedCoord>(){}.getType();
                        SavedCoord savedCoord = gson.fromJson(savedCoordJson,type);
                        Log.d(TAG, "onLocationResult: saved Lat: " + savedCoord.getLat() + " , cur lat: " + location.getLatitude());
                        Log.d(TAG, "onLocationResult: saved lon: " + savedCoord.getLon() + " , cur lon: " + location.getLongitude());
                        if(savedCoord.getLat() != round(location.getLatitude(),2) || savedCoord.getLon() != round(location.getLongitude(),2)){
                            updateLocation(location);
                        }
                        else{
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                            Log.d(TAG, "onLocationResult: Location unchanged.");
                        }
                    }
                    Log.d(TAG, "onLocationResult: lat: " + location.getLatitude() + ", lon: " + location.getLongitude());
                }
            }
        };

    }

    public  double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
