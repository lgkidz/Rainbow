package com.OdiousPanda.rainbow.MainFragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.OdiousPanda.rainbow.R;
import com.OdiousPanda.rainbow.Utilities.PreferencesUtil;
import com.OdiousPanda.rainbow.Widgets.NormalWidget;
import com.google.android.material.snackbar.Snackbar;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import java.util.Objects;


public class SettingFragment extends Fragment implements View.OnClickListener {

    public static final String ACTION_UPDATE_UNIT = "Rainbow.update.unit";
    public static final String ACTION_UPDATE_BACKGROUND = "Rainbow.update.background";
    @SuppressLint("StaticFieldLeak")
    private static SettingFragment instance;
    public boolean aboutMeShowing = false;
    private String currentTempUnit;
    private String currentDistanceUnit;
    private String currentSpeedUnit;
    private String currentPressureUnit;
    private String currentBackgroundSetting;
    private boolean isExplicit;
    private TextView tvRate;
    private TextView tvAbout;
    private Button btnC;
    private Button btnF;
    private Button btnKm;
    private Button btnMi;
    private Button btnKmph;
    private Button btnMiph;
    private Button btnBananaph;
    private Button btnBanana;
    private Button btnPsi;
    private Button btnMmhg;
    private Button btnDepress;
    private Button btnScientist;
    private Button btnImNot;
    private Button btnHellYeah;
    private Button btnColor;
    private Button btnPicture;
    private Button btnPictureRandom;
    private SlideUp aboutMeSlideUp;
    private int activeButtonColor = Color.argb(255, 255, 255, 255);
    private int buttonColor = Color.argb(255, 255, 255, 255);
    private int buttonTextColor = Color.argb(255, 255, 255, 255);
    private int activeButtonTextColor = Color.argb(255, 255, 255, 255);

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment getInstance() {
        if (instance == null) {
            instance = new SettingFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        initViews(v);
        getSetting();
        return v;
    }

    private void initViews(View v) {
        activeButtonColor = ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.default_active_button_bg);
        buttonColor = ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.default_button_bg);
        CoordinatorLayout settingScreenLayout = v.findViewById(R.id.setting_layout);
        tvRate = v.findViewById(R.id.tv_rate);
        tvAbout = v.findViewById(R.id.tv_about);
        btnC = v.findViewById(R.id.btn_c);
        btnF = v.findViewById(R.id.btn_f);
        btnKm = v.findViewById(R.id.btn_km);
        btnMi = v.findViewById(R.id.btn_mi);
        btnKmph = v.findViewById(R.id.btn_kmph);
        btnMiph = v.findViewById(R.id.btn_miph);
        btnBananaph = v.findViewById(R.id.btn_bananaph);
        btnBanana = v.findViewById(R.id.btn_bananas);
        btnPsi = v.findViewById(R.id.btn_psi);
        btnMmhg = v.findViewById(R.id.btn_mmhg);
        btnDepress = v.findViewById(R.id.btn_depress);
        btnScientist = v.findViewById(R.id.btn_scientist);
        btnImNot = v.findViewById(R.id.btn_im_not);
        btnHellYeah = v.findViewById(R.id.btn_hell_yeah);
        btnColor = v.findViewById(R.id.btn_random_color);
        btnPicture = v.findViewById(R.id.btn_picture);
        btnPictureRandom = v.findViewById(R.id.btn_picture_random);

        final ConstraintLayout aboutMeLayout = v.findViewById(R.id.about_me_layout);
        ImageView closeAboutMeBtn = v.findViewById(R.id.btn_close_about);
        aboutMeSlideUp = new SlideUpBuilder(aboutMeLayout)
                .withStartState(SlideUp.State.HIDDEN)
                .withStartGravity(Gravity.BOTTOM)
                .withSlideFromOtherView(settingScreenLayout)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                        aboutMeShowing = percent != 100;
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {

                    }
                })
                .build();
        tvRate.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        closeAboutMeBtn.setOnClickListener(this);

        btnC.setOnClickListener(this);
        btnF.setOnClickListener(this);
        btnKm.setOnClickListener(this);
        btnKmph.setOnClickListener(this);
        btnMiph.setOnClickListener(this);
        btnBananaph.setOnClickListener(this);
        btnBanana.setOnClickListener(this);
        btnPsi.setOnClickListener(this);
        btnMmhg.setOnClickListener(this);
        btnDepress.setOnClickListener(this);
        btnScientist.setOnClickListener(this);
        btnMi.setOnClickListener(this);
        btnImNot.setOnClickListener(this);
        btnHellYeah.setOnClickListener(this);
        btnColor.setOnClickListener(this);
        btnPicture.setOnClickListener(this);
        btnPictureRandom.setOnClickListener(this);
    }

    private void rateThisApp() {
        Snackbar.make(tvRate, "The developer is still working on this feature", Snackbar.LENGTH_SHORT).show();
    }

    private void showAboutMeDialog() {
        aboutMeSlideUp.show();
        aboutMeShowing = true;
    }

    public void closeAboutMeDialog() {
        aboutMeSlideUp.hide();
        aboutMeShowing = false;
    }

    private void getSetting() {
        currentTempUnit = PreferencesUtil.getTemperatureUnit(Objects.requireNonNull(getActivity()));
        currentDistanceUnit = PreferencesUtil.getDistanceUnit(getActivity());
        currentSpeedUnit = PreferencesUtil.getSpeedUnit(getActivity());
        currentPressureUnit = PreferencesUtil.getPressureUnit(getActivity());
        isExplicit = PreferencesUtil.isExplicit(getActivity());
        currentBackgroundSetting = PreferencesUtil.getBackgroundSetting(getActivity());
        colorThoseButtons();
    }

    private void colorThoseButtons() {
        if (currentTempUnit.equals(getString(R.string.temp_setting_degree_c))) {
            updateTempButtonColor(btnC.getId());
        } else if (currentTempUnit.equals(getString(R.string.temp_setting_degree_f))) {
            updateTempButtonColor(btnF.getId());
        } else {
            updateTempButtonColor(btnScientist.getId());
        }

        if (currentDistanceUnit.equals(getString(R.string.km))) {
            updateDistanceButtonColor(btnKm.getId());
        } else if (currentDistanceUnit.equals(getString(R.string.mi))) {
            updateDistanceButtonColor(btnMi.getId());
        } else {
            updateDistanceButtonColor(btnBanana.getId());
        }

        if (currentSpeedUnit.equals(getString(R.string.kmph))) {
            updateSpeedButtonColor(btnKmph.getId());
        } else if (currentSpeedUnit.equals(getString(R.string.miph))) {
            updateSpeedButtonColor(btnMiph.getId());
        } else {
            updateSpeedButtonColor(btnBananaph.getId());
        }

        if (currentPressureUnit.equals(getString(R.string.psi))) {
            updatePressureButtonColor(btnPsi.getId());
        } else if (currentPressureUnit.equals(getString(R.string.mmhg))) {
            updatePressureButtonColor(btnMmhg.getId());
        } else {
            updatePressureButtonColor(btnDepress.getId());
        }

        if (currentBackgroundSetting.equals(PreferencesUtil.BACKGROUND_COLOR)) {
            updateBackgroundSettingButtonColor(btnColor.getId());
        } else if (currentBackgroundSetting.equals(PreferencesUtil.BACKGROUND_PICTURE)) {
            updateBackgroundSettingButtonColor(btnPicture.getId());
        } else {
            updateBackgroundSettingButtonColor(btnPictureRandom.getId());
        }

        if (isExplicit) {
            updateExplicitButtonColor(btnImNot.getId());
        } else {
            updateExplicitButtonColor(btnHellYeah.getId());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_c:
            case R.id.btn_f:
            case R.id.btn_scientist: {
                changeTempUnit(id);
                break;
            }
            case R.id.btn_km:
            case R.id.btn_mi:
            case R.id.btn_bananas: {
                changeDistanceUnit(id);
                break;
            }
            case R.id.btn_kmph:
            case R.id.btn_miph:
            case R.id.btn_bananaph: {
                changeSpeedUnit(id);
                break;
            }
            case R.id.btn_psi:
            case R.id.btn_mmhg:
            case R.id.btn_depress: {
                changePressureUnit(id);
                break;
            }
            case R.id.btn_im_not:
            case R.id.btn_hell_yeah: {
                changeExplicitSetting(id);
                break;
            }
            case R.id.tv_rate: {
                rateThisApp();
                break;
            }
            case R.id.tv_about: {
                showAboutMeDialog();
                break;
            }
            case R.id.btn_close_about: {
                closeAboutMeDialog();
                break;
            }
            case R.id.btn_random_color:
            case R.id.btn_picture:
            case R.id.btn_picture_random: {
                changeBackgroundSetting(id);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void changeTempUnit(int id) {
        String pref = PreferencesUtil.TEMPERATURE_UNIT;
        if (id == R.id.btn_c) {
            updateSharedPref(pref, getString(R.string.temp_setting_degree_c));
        } else if (id == R.id.btn_f) {
            updateSharedPref(pref, getString(R.string.temp_setting_degree_f));
        } else {
            updateSharedPref(pref, getString(R.string.temp_setting_degree_k));
        }
        HomeScreenFragment.getInstance().updateUnit();
        Intent updateWidgetIntent = new Intent(NormalWidget.ACTION_UPDATE);
        Objects.requireNonNull(getActivity()).sendBroadcast(updateWidgetIntent);
        updateTempButtonColor(id);
        currentTempUnit = PreferencesUtil.getTemperatureUnit(getActivity());
        Intent updateUnitBroadcast = new Intent();
        updateUnitBroadcast.setAction(ACTION_UPDATE_UNIT);
        getActivity().sendBroadcast(updateUnitBroadcast);
    }

    private void updateTempButtonColor(int id) {
        if (id == R.id.btn_c) {
            btnC.setBackgroundColor(activeButtonColor);
            btnF.setBackgroundColor(buttonColor);
            btnScientist.setBackgroundColor(buttonColor);
            btnC.setTextColor(activeButtonTextColor);
            btnF.setTextColor(buttonTextColor);
            btnScientist.setTextColor(buttonTextColor);
        } else if (id == R.id.btn_f) {
            btnC.setBackgroundColor(buttonColor);
            btnF.setBackgroundColor(activeButtonColor);
            btnScientist.setBackgroundColor(buttonColor);
            btnC.setTextColor(buttonTextColor);
            btnF.setTextColor(activeButtonTextColor);
            btnScientist.setTextColor(buttonTextColor);
        } else {
            btnC.setBackgroundColor(buttonColor);
            btnF.setBackgroundColor(buttonColor);
            btnScientist.setBackgroundColor(activeButtonColor);
            btnC.setTextColor(buttonTextColor);
            btnF.setTextColor(buttonTextColor);
            btnScientist.setTextColor(activeButtonTextColor);
        }
    }

    private void changeDistanceUnit(int id) {
        String pref = PreferencesUtil.DISTANCE_UNIT;
        if (id == R.id.btn_km) {
            updateSharedPref(pref, getString(R.string.km));
        } else if (id == R.id.btn_mi) {
            updateSharedPref(pref, getString(R.string.mi));
        } else {
            updateSharedPref(pref, getString(R.string.bananas));
        }

        updateDistanceButtonColor(id);
        Intent updateUnitBroadcast = new Intent();
        updateUnitBroadcast.setAction(ACTION_UPDATE_UNIT);
        Objects.requireNonNull(getActivity()).sendBroadcast(updateUnitBroadcast);
        currentDistanceUnit = PreferencesUtil.getDistanceUnit(Objects.requireNonNull(getActivity()));
    }

    private void updateDistanceButtonColor(int id) {
        if (id == R.id.btn_km) {
            btnKm.setBackgroundColor(activeButtonColor);
            btnMi.setBackgroundColor(buttonColor);
            btnBanana.setBackgroundColor(buttonColor);
            btnKm.setTextColor(activeButtonTextColor);
            btnMi.setTextColor(buttonTextColor);
            btnBanana.setTextColor(buttonTextColor);
        } else if (id == R.id.btn_mi) {
            btnKm.setBackgroundColor(buttonColor);
            btnMi.setBackgroundColor(activeButtonColor);
            btnBanana.setBackgroundColor(buttonColor);
            btnKm.setTextColor(buttonTextColor);
            btnMi.setTextColor(activeButtonTextColor);
            btnBanana.setTextColor(buttonTextColor);
        } else {
            btnKm.setBackgroundColor(buttonColor);
            btnMi.setBackgroundColor(buttonColor);
            btnBanana.setBackgroundColor(activeButtonColor);
            btnKm.setTextColor(buttonTextColor);
            btnMi.setTextColor(buttonTextColor);
            btnBanana.setTextColor(activeButtonTextColor);
        }
    }

    private void changeSpeedUnit(int id) {
        String pref = PreferencesUtil.SPEED_UNIT;
        if (id == R.id.btn_kmph) {
            updateSharedPref(pref, getString(R.string.kmph));
        } else if (id == R.id.btn_miph) {
            updateSharedPref(pref, getString(R.string.miph));
        } else {
            updateSharedPref(pref, getString(R.string.banana_h));
        }
        updateSpeedButtonColor(id);
        currentSpeedUnit = PreferencesUtil.getSpeedUnit(Objects.requireNonNull(getActivity()));
    }

    private void updateSpeedButtonColor(int id) {
        if (id == R.id.btn_kmph) {
            btnKmph.setBackgroundColor(activeButtonColor);
            btnMiph.setBackgroundColor(buttonColor);
            btnBananaph.setBackgroundColor(buttonColor);
            btnKmph.setTextColor(activeButtonTextColor);
            btnMiph.setTextColor(buttonTextColor);
            btnBananaph.setTextColor(buttonTextColor);
        } else if (id == R.id.btn_miph) {
            btnKmph.setBackgroundColor(buttonColor);
            btnMiph.setBackgroundColor(activeButtonColor);
            btnBananaph.setBackgroundColor(buttonColor);
            btnKmph.setTextColor(buttonTextColor);
            btnMiph.setTextColor(activeButtonTextColor);
            btnBananaph.setTextColor(buttonTextColor);
        } else {
            btnKmph.setBackgroundColor(buttonColor);
            btnMiph.setBackgroundColor(buttonColor);
            btnBananaph.setBackgroundColor(activeButtonColor);
            btnKmph.setTextColor(buttonTextColor);
            btnMiph.setTextColor(buttonTextColor);
            btnBananaph.setTextColor(activeButtonTextColor);
        }
    }

    private void changePressureUnit(int id) {
        String pref = PreferencesUtil.PRESSURE_UNIT;
        if (id == R.id.btn_psi) {
            updateSharedPref(pref, getString(R.string.psi));
        } else if (id == R.id.btn_mmhg) {
            updateSharedPref(pref, getString(R.string.mmhg));
        } else {
            updateSharedPref(pref, getString(R.string.depression_unit));
        }
        updatePressureButtonColor(id);
        currentPressureUnit = PreferencesUtil.getPressureUnit(Objects.requireNonNull(getActivity()));
    }

    private void updatePressureButtonColor(int id) {
        if (id == R.id.btn_psi) {
            btnPsi.setBackgroundColor(activeButtonColor);
            btnMmhg.setBackgroundColor(buttonColor);
            btnDepress.setBackgroundColor(buttonColor);
            btnPsi.setTextColor(activeButtonTextColor);
            btnMmhg.setTextColor(buttonTextColor);
            btnDepress.setTextColor(buttonTextColor);
        } else if (id == R.id.btn_mmhg) {
            btnPsi.setBackgroundColor(buttonColor);
            btnMmhg.setBackgroundColor(activeButtonColor);
            btnDepress.setBackgroundColor(buttonColor);
            btnPsi.setTextColor(buttonTextColor);
            btnMmhg.setTextColor(activeButtonTextColor);
            btnDepress.setTextColor(buttonTextColor);
        } else {
            btnPsi.setBackgroundColor(buttonColor);
            btnMmhg.setBackgroundColor(buttonColor);
            btnDepress.setBackgroundColor(activeButtonColor);
            btnPsi.setTextColor(buttonTextColor);
            btnMmhg.setTextColor(buttonTextColor);
            btnDepress.setTextColor(activeButtonTextColor);
        }
    }

    private void updateBackgroundSettingButtonColor(int id) {
        if (id == R.id.btn_random_color) {
            btnColor.setBackgroundColor(activeButtonColor);
            btnPicture.setBackgroundColor(buttonColor);
            btnPictureRandom.setBackgroundColor(buttonColor);
            btnColor.setTextColor(activeButtonTextColor);
            btnPicture.setTextColor(buttonTextColor);
            btnPictureRandom.setTextColor(buttonTextColor);
        } else if (id == R.id.btn_picture) {
            btnColor.setBackgroundColor(buttonColor);
            btnPicture.setBackgroundColor(activeButtonColor);
            btnPictureRandom.setBackgroundColor(buttonColor);
            btnColor.setTextColor(buttonTextColor);
            btnPicture.setTextColor(activeButtonTextColor);
            btnPictureRandom.setTextColor(buttonTextColor);
        } else {
            btnColor.setBackgroundColor(buttonColor);
            btnPicture.setBackgroundColor(buttonColor);
            btnPictureRandom.setBackgroundColor(activeButtonColor);
            btnColor.setTextColor(buttonTextColor);
            btnPicture.setTextColor(buttonTextColor);
            btnPictureRandom.setTextColor(activeButtonTextColor);
        }
    }

    private void changeExplicitSetting(int id) {
        String pref = PreferencesUtil.EXPLICIT_SETTING;
        if (id == R.id.btn_im_not) {
            updateSharedPref(pref, getString(R.string.im_not));
        } else {
            updateSharedPref(pref, getString(R.string.hell_yeah));
        }
        updateExplicitButtonColor(id);
        HomeScreenFragment.getInstance().updateExplicitSetting();
        Intent updateWidgetIntent = new Intent(NormalWidget.ACTION_UPDATE);
        Objects.requireNonNull(getActivity()).sendBroadcast(updateWidgetIntent);
    }

    private void updateExplicitButtonColor(int id) {
        if (id == R.id.btn_im_not) {
            btnImNot.setBackgroundColor(activeButtonColor);
            btnHellYeah.setBackgroundColor(buttonColor);
            btnImNot.setTextColor(activeButtonTextColor);
            btnHellYeah.setTextColor(buttonTextColor);
        } else {
            btnImNot.setBackgroundColor(buttonColor);
            btnHellYeah.setBackgroundColor(activeButtonColor);
            btnImNot.setTextColor(buttonTextColor);
            btnHellYeah.setTextColor(activeButtonTextColor);
        }
    }

    private void changeBackgroundSetting(int id) {
        if (id == R.id.btn_random_color) {
            PreferencesUtil.setBackgroundSetting(Objects.requireNonNull(getActivity()), PreferencesUtil.BACKGROUND_COLOR);
        } else if (id == R.id.btn_picture) {
            PreferencesUtil.setBackgroundSetting(Objects.requireNonNull(getActivity()), PreferencesUtil.BACKGROUND_PICTURE);
        } else if (id == R.id.btn_picture_random) {
            PreferencesUtil.setBackgroundSetting(Objects.requireNonNull(getActivity()), PreferencesUtil.BACKGROUND_PICTURE_RANDOM);
        }
        updateBackgroundSettingButtonColor(id);
        Intent updateBackgroundBroadcast = new Intent();
        updateBackgroundBroadcast.setAction(ACTION_UPDATE_BACKGROUND);
        Objects.requireNonNull(getActivity()).sendBroadcast(updateBackgroundBroadcast);
        currentBackgroundSetting = PreferencesUtil.getBackgroundSetting(getActivity());
    }

    private void updateSharedPref(String pref, String value) {
        if (pref.equals(PreferencesUtil.EXPLICIT_SETTING)) {
            if (value.equals(getString(R.string.im_not))) {
                PreferencesUtil.setExplicitSetting(Objects.requireNonNull(getActivity()), true);
            } else {
                PreferencesUtil.setExplicitSetting(Objects.requireNonNull(getActivity()), false);
            }
        } else {
            PreferencesUtil.setUnitSetting(Objects.requireNonNull(getActivity()), pref, value);
        }
        DetailsFragment.getInstance().updateUnit();
    }

}
