package com.OdiousPanda.rainbow.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.OdiousPanda.rainbow.Adapters.WelcomePagerAdapter;
import com.OdiousPanda.rainbow.DataModel.Coordinate;
import com.OdiousPanda.rainbow.R;
import com.OdiousPanda.rainbow.Utilities.PreferencesUtil;
import com.OdiousPanda.rainbow.ViewModels.WeatherViewModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Locale;

import mumayank.com.airlocationlibrary.AirLocation;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "weatherA";
    private WeatherViewModel weatherViewModel;
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Button btnNext;
    private boolean locationUpdated = false;
    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (!locationUpdated) {
                updateCurrentLocation();
                locationUpdated = true;
            }
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == 2) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private ConstraintLayout dotsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Checking for first time launch - before calling setContentView()
        if (PreferencesUtil.isNotFirstTimeLaunch(this)) {
            launchHomeScreen();
            finish();
        }

        setContentView(R.layout.activity_welcome);
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btn_next);
        viewPager.setOffscreenPageLimit(3);
        dotsContainer = findViewById(R.id.dotsContainer);

        // adding bottom dots
        addBottomDots(0);

        WelcomePagerAdapter adapter = new WelcomePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!locationUpdated) {
                    updateCurrentLocation();
                    locationUpdated = true;
                }

                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < 3) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

        requestPermission();
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[3];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[0]);
            dotsLayout.addView(dots[i]);
        }

        dots[currentPage].setTextColor(colorsActive[0]);

    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        PreferencesUtil.setNotFirstTimeLaunch(this, true);
        Intent mainActivityIntent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
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
                    Geocoder geo = new Geocoder(WelcomeActivity.this, Locale.getDefault());
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

    private void requestPermission() {
        Dexter.withActivity(WelcomeActivity.this)
                //There might need more permissions in the future
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (!report.areAllPermissionsGranted()) {
                            showNeededPermissionDialog();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingDialog();
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
        builder.setMessage(getString(R.string.app_need_permissions_to_run));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.message_need_permission));
        builder.setMessage(getString(R.string.message_grant_permission));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.show();
    }

    /**
     * Go to App's details setting in default phone setting
     */
    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        finish();
    }
}
