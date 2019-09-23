package com.OdiousPanda.rainbow.MainFragments;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.OdiousPanda.rainbow.R;
import com.OdiousPanda.rainbow.Utilities.Constants;
import com.OdiousPanda.rainbow.Utilities.TextUtil;
import com.OdiousPanda.rainbow.Utilities.NotificationUtil;
import com.OdiousPanda.rainbow.Utilities.PreferencesUtil;
import com.OdiousPanda.rainbow.Widgets.NormalWidget;
import com.google.android.material.snackbar.Snackbar;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import java.util.Calendar;
import java.util.Objects;


public class SettingFragment extends Fragment implements View.OnClickListener {


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
    private Switch dailyNotificationSwitch;
    private TextView dailyNotificationTime;
    private TimePickerDialog timePickerDialog;
    private NotificationUtil notificationUtil;
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
        dailyNotificationSwitch = v.findViewById(R.id.daily_notification_switch);
        dailyNotificationTime = v.findViewById(R.id.daily_notification_time);

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
        setupNotificationSetting();
        colorThoseButtons();
    }

    private void setupNotificationSetting() {
        if (PreferencesUtil.getNotificationSetting(Objects.requireNonNull(getActivity())).equals(PreferencesUtil.NOTIFICATION_SETTING_ON)) {
            dailyNotificationSwitch.setChecked(true);
            dailyNotificationTime.setTextColor(Color.BLACK);
        } else {
            dailyNotificationSwitch.setChecked(false);
            dailyNotificationTime.setTextColor(ContextCompat.getColor(getActivity(), R.color.halfSnappedBlack));
        }
        notificationUtil = new NotificationUtil(getActivity());
        dailyNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferencesUtil.setNotificationSetting(Objects.requireNonNull(getActivity()), PreferencesUtil.NOTIFICATION_SETTING_ON);
                    dailyNotificationTime.setTextColor(Color.BLACK);
                    notificationUtil.startDailyNotification();
                } else {
                    PreferencesUtil.setNotificationSetting(Objects.requireNonNull(getActivity()), PreferencesUtil.NOTIFICATION_SETTING_OFF);
                    dailyNotificationTime.setTextColor(ContextCompat.getColor(getActivity(), R.color.halfSnappedBlack));
                    notificationUtil.cancelDailyNotification();
                }
            }
        });

        Calendar calendar = PreferencesUtil.getNotificationTime(getActivity());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        String timeString = getString(R.string.time_place_holder) + " " + TextUtil.getTimeStringPretty(hour, minute);
        dailyNotificationTime.setText(timeString);
        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                String time = getString(R.string.time_place_holder) + " " + TextUtil.getTimeStringPretty(hourOfDay, min);
                dailyNotificationTime.setText(time);
                PreferencesUtil.setNotificationTime(Objects.requireNonNull(getActivity()), hourOfDay, min);
                notificationUtil.cancelDailyNotification();
                notificationUtil.startDailyNotification();
            }
        }, hour, minute, false);

        dailyNotificationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferencesUtil.getNotificationSetting(Objects.requireNonNull(getActivity())).equals(PreferencesUtil.NOTIFICATION_SETTING_ON)) {
                    timePickerDialog.show();
                }
            }
        });
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
        updateUnitBroadcast.setAction(Constants.ACTION_UPDATE_UNIT);
        getActivity().sendBroadcast(updateUnitBroadcast);
    }

    private void updateTempButtonColor(int id) {
        if (id == R.id.btn_c) {
            btnC.setBackgroundResource(R.drawable.left_button_active);
            btnF.setBackgroundResource(R.drawable.middle_button);
            btnScientist.setBackgroundResource(R.drawable.right_button);
            btnC.setTextColor(activeButtonTextColor);
            btnF.setTextColor(buttonTextColor);
            btnScientist.setTextColor(buttonTextColor);
        } else if (id == R.id.btn_f) {
            btnC.setBackgroundResource(R.drawable.left_button);
            btnF.setBackgroundResource(R.drawable.middle_button_active);
            btnScientist.setBackgroundResource(R.drawable.right_button);
            btnC.setTextColor(buttonTextColor);
            btnF.setTextColor(activeButtonTextColor);
            btnScientist.setTextColor(buttonTextColor);
        } else {
            btnC.setBackgroundResource(R.drawable.left_button);
            btnF.setBackgroundResource(R.drawable.middle_button);
            btnScientist.setBackgroundResource(R.drawable.right_button_active);
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
        updateUnitBroadcast.setAction(Constants.ACTION_UPDATE_UNIT);
        Objects.requireNonNull(getActivity()).sendBroadcast(updateUnitBroadcast);
        currentDistanceUnit = PreferencesUtil.getDistanceUnit(Objects.requireNonNull(getActivity()));
    }

    private void updateDistanceButtonColor(int id) {
        if (id == R.id.btn_km) {
            btnKm.setBackgroundResource(R.drawable.left_button_active);
            btnMi.setBackgroundResource(R.drawable.middle_button);
            btnBanana.setBackgroundResource(R.drawable.right_button);
            btnKm.setTextColor(activeButtonTextColor);
            btnMi.setTextColor(buttonTextColor);
            btnBanana.setTextColor(buttonTextColor);
        } else if (id == R.id.btn_mi) {
            btnKm.setBackgroundResource(R.drawable.left_button);
            btnMi.setBackgroundResource(R.drawable.middle_button_active);
            btnBanana.setBackgroundResource(R.drawable.right_button);
            btnKm.setTextColor(buttonTextColor);
            btnMi.setTextColor(activeButtonTextColor);
            btnBanana.setTextColor(buttonTextColor);
        } else {
            btnKm.setBackgroundResource(R.drawable.left_button);
            btnMi.setBackgroundResource(R.drawable.middle_button);
            btnBanana.setBackgroundResource(R.drawable.right_button_active);
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
            btnKmph.setBackgroundResource(R.drawable.left_button_active);
            btnMiph.setBackgroundResource(R.drawable.middle_button);
            btnBananaph.setBackgroundResource(R.drawable.right_button);
            btnKmph.setTextColor(activeButtonTextColor);
            btnMiph.setTextColor(buttonTextColor);
            btnBananaph.setTextColor(buttonTextColor);
        } else if (id == R.id.btn_miph) {
            btnKmph.setBackgroundResource(R.drawable.left_button);
            btnMiph.setBackgroundResource(R.drawable.middle_button_active);
            btnBananaph.setBackgroundResource(R.drawable.right_button);
            btnKmph.setTextColor(buttonTextColor);
            btnMiph.setTextColor(activeButtonTextColor);
            btnBananaph.setTextColor(buttonTextColor);
        } else {
            btnKmph.setBackgroundResource(R.drawable.left_button);
            btnMiph.setBackgroundResource(R.drawable.middle_button);
            btnBananaph.setBackgroundResource(R.drawable.right_button_active);
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
            btnPsi.setBackgroundResource(R.drawable.left_button_active);
            btnMmhg.setBackgroundResource(R.drawable.middle_button);
            btnDepress.setBackgroundResource(R.drawable.right_button);
            btnPsi.setTextColor(activeButtonTextColor);
            btnMmhg.setTextColor(buttonTextColor);
            btnDepress.setTextColor(buttonTextColor);
        } else if (id == R.id.btn_mmhg) {
            btnPsi.setBackgroundResource(R.drawable.left_button);
            btnMmhg.setBackgroundResource(R.drawable.middle_button_active);
            btnDepress.setBackgroundResource(R.drawable.right_button);
            btnPsi.setTextColor(buttonTextColor);
            btnMmhg.setTextColor(activeButtonTextColor);
            btnDepress.setTextColor(buttonTextColor);
        } else {
            btnPsi.setBackgroundResource(R.drawable.left_button);
            btnMmhg.setBackgroundResource(R.drawable.middle_button);
            btnDepress.setBackgroundResource(R.drawable.right_button_active);
            btnPsi.setTextColor(buttonTextColor);
            btnMmhg.setTextColor(buttonTextColor);
            btnDepress.setTextColor(activeButtonTextColor);
        }
    }

    private void updateBackgroundSettingButtonColor(int id) {
        if (id == R.id.btn_random_color) {
            btnColor.setBackgroundResource(R.drawable.left_button_active);
            btnPicture.setBackgroundResource(R.drawable.middle_button);
            btnPictureRandom.setBackgroundResource(R.drawable.right_button);
            btnColor.setTextColor(activeButtonTextColor);
            btnPicture.setTextColor(buttonTextColor);
            btnPictureRandom.setTextColor(buttonTextColor);
        } else if (id == R.id.btn_picture) {
            btnColor.setBackgroundResource(R.drawable.left_button);
            btnPicture.setBackgroundResource(R.drawable.middle_button_active);
            btnPictureRandom.setBackgroundResource(R.drawable.right_button);
            btnColor.setTextColor(buttonTextColor);
            btnPicture.setTextColor(activeButtonTextColor);
            btnPictureRandom.setTextColor(buttonTextColor);
        } else {
            btnColor.setBackgroundResource(R.drawable.left_button);
            btnPicture.setBackgroundResource(R.drawable.middle_button);
            btnPictureRandom.setBackgroundResource(R.drawable.right_button_active);
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
            btnImNot.setBackgroundResource(R.drawable.left_button_active);
            btnHellYeah.setBackgroundResource(R.drawable.right_button);
            btnImNot.setTextColor(activeButtonTextColor);
            btnHellYeah.setTextColor(buttonTextColor);
        } else {
            btnImNot.setBackgroundResource(R.drawable.left_button);
            btnHellYeah.setBackgroundResource(R.drawable.right_button_active);
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
        updateBackgroundBroadcast.setAction(Constants.ACTION_UPDATE_BACKGROUND);
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
