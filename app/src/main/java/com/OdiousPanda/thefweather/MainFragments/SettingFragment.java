package com.OdiousPanda.thefweather.MainFragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.OdiousPanda.thefweather.Model.AQI.P;
import com.OdiousPanda.thefweather.NormalWidget;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.MyColorUtil;


public class SettingFragment extends Fragment implements View.OnClickListener {

    public static SettingFragment instance;

    private String currentTempUnit;
    private String currentDistanceUnit;
    private String currentSpeedUnit;
    private String currentPressureUnit;

    private SharedPreferences sharedPreferences;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment getInstance() {
        if (instance == null){
            instance = new SettingFragment();
        }
        return instance;
    }

    private TextView tvSetting;
    private TextView tvTemperature;
    private TextView tvDistance;
    private TextView tvSpeed;
    private TextView tvPressure;
    private TextView tvHelp;
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
    private Button btnHelpDev;
    private Button btnHelpMe;

    private int activeButtonColor = Color.argb(255,255,255,255);
    private int buttonColor = Color.argb(255,255,255,255);
    private int textColor = Color.argb(255,0,0,0);
    private int buttonTextColor = Color.argb(255,0,0,0);
    private int activeButtonTextColor = Color.argb(255,0,0,0);;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key_string), Context.MODE_PRIVATE);

        initViews(v);
        getSetting();

        return v;
    }

    private void initViews(View v){
        tvSetting = v.findViewById(R.id.tv_setting);
        tvTemperature = v.findViewById(R.id.tv_temp_setting);
        tvDistance = v.findViewById(R.id.tv_distance_setting);
        tvSpeed = v.findViewById(R.id.tv_speed_setting);
        tvPressure = v.findViewById(R.id.tv_pressure_setting);
        tvHelp = v.findViewById(R.id.tv_help_setting);
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
        btnHelpDev = v.findViewById(R.id.btn_help_dev);
        btnHelpMe = v.findViewById(R.id.btn_help_me);

        tvRate.setOnClickListener(this);
        tvAbout.setOnClickListener(this);

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
        btnHelpDev.setOnClickListener(this);
        btnMi.setOnClickListener(this);
        btnHelpMe.setOnClickListener(this);

        btnHelpDev.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    btnHelpDev.setBackgroundColor(buttonColor);
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnHelpDev.setBackgroundColor(activeButtonColor);
                    btnHelpMe.setBackgroundColor(buttonColor);
                }
                return false;
            }
        });

        btnHelpMe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    btnHelpMe.setBackgroundColor(buttonColor);
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnHelpMe.setBackgroundColor(activeButtonColor);
                    btnHelpDev.setBackgroundColor(buttonColor);
                }
                return false;
            }
        });
    }

    private void getSetting(){
        currentTempUnit = sharedPreferences.getString(getString(R.string.pref_temp),getString(R.string.temp_setting_degree_c));
        currentDistanceUnit = sharedPreferences.getString(getString(R.string.pref_distance),getString(R.string.km));
        currentSpeedUnit = sharedPreferences.getString(getString(R.string.pref_speed),getString(R.string.kmph));
        currentPressureUnit = sharedPreferences.getString(getString(R.string.pref_pressure),getString(R.string.psi));
        colorThoseButtons();
        colorThoseTextView();
    }

    private void colorThoseTextView(){
        tvAbout.setTextColor(textColor);
        tvDistance.setTextColor(textColor);
        tvTemperature.setTextColor(textColor);
        tvSetting.setTextColor(textColor);
        tvSpeed.setTextColor(textColor);
        tvPressure.setTextColor(textColor);
        tvRate.setTextColor(textColor);
        tvHelp.setTextColor(textColor);
    }

    private void colorThoseButtons(){
        if(currentTempUnit.equals(getString(R.string.temp_setting_degree_c))){
            updateTempButtonColor(btnC.getId());
        }
        else if(currentTempUnit.equals(getString(R.string.temp_setting_degree_f))){
            updateTempButtonColor(btnF.getId());
        }
        else{
            updateTempButtonColor(btnScientist.getId());
        }

        if(currentDistanceUnit.equals(getString(R.string.km))){
            updateDistanceButtonColor(btnKm.getId());
        }
        else if(currentDistanceUnit.equals(getString(R.string.mi))){
            updateDistanceButtonColor(btnMi.getId());
        }
        else{
            updateDistanceButtonColor(btnBanana.getId());
        }

        if(currentSpeedUnit.equals(getString(R.string.kmph))){
            updateSpeedButtonColor(btnKmph.getId());
        }
        else if(currentSpeedUnit.equals(getString(R.string.miph))){
            updateSpeedButtonColor(btnMiph.getId());
        }
        else{
            updateSpeedButtonColor(btnBananaph.getId());
        }

        if(currentPressureUnit.equals(getString(R.string.psi))){
            updatePressureButtonColor(btnPsi.getId());
        }
        else if(currentPressureUnit.equals(getString(R.string.mmhg))){
            updatePressureButtonColor(btnMmhg.getId());
        }
        else{
            updatePressureButtonColor(btnDepress.getId());
        }

        updateHelpButtonColor();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_c:{
                changeTempUnit(id);
                break;
            }
            case R.id.btn_f:{
                changeTempUnit(id);
                break;
            }
            case R.id.btn_scientist:{
                changeTempUnit(id);
                break;
            }
            case R.id.btn_km:{
                changeDistanceUnit(id);
                break;
            }
            case R.id.btn_mi:{
                changeDistanceUnit(id);
                break;
            }
            case R.id.btn_bananas:{
                changeDistanceUnit(id);
                break;
            }
            case R.id.btn_kmph:{
                changeSpeedUnit(id);
                break;
            }
            case R.id.btn_miph:{
                changeSpeedUnit(id);
                break;
            }
            case R.id.btn_bananaph:{
                changeSpeedUnit(id);
                break;
            }
            case R.id.btn_psi:{
                changePressureUnit(id);
                break;
            }
            case R.id.btn_mmhg:{
                changePressureUnit(id);
                break;
            }
            case R.id.btn_depress:{
                changePressureUnit(id);
                break;
            }
            case R.id.btn_help_dev:{

                break;
            }
            case R.id.btn_help_me:{

                break;
            }
            case R.id.tv_rate:{

                break;
            }
            case R.id.tv_about:{

                break;
            }
            default: break;
        }
    }

    public void updateColorTheme(int[] argb){
        activeButtonColor = MyColorUtil.invertColor(argb);
        activeButtonTextColor = MyColorUtil.blackOrWhiteOf(new int[]{255,255 - argb[1],255 - argb[2],255 - argb[3]});
        textColor = MyColorUtil.blackOrWhiteOf(argb);
        int r = (int) (argb[1] * 0.8);
        int g = (int) (argb[2] * 0.8);
        int b = (int) (argb[3] * 0.75);
        buttonColor = Color.argb(255,r,g,b);
        buttonTextColor = MyColorUtil.blackOrWhiteOf(new int[]{255,r,g,b});
        getSetting();
    }

    private void changeTempUnit(int id){
        String pref = getString(R.string.pref_temp);
        if(id == R.id.btn_c){
            updateSharedPref(pref,getString(R.string.temp_setting_degree_c));
        }
        else if(id == R.id.btn_f){
            updateSharedPref(pref,getString(R.string.temp_setting_degree_f));
        }
        else{
            updateSharedPref(pref,getString(R.string.temp_setting_degree_k));
        }
        HomeScreenFragment.getInstance().updateUnit();
        Intent updateWidgetIntent = new Intent(NormalWidget.ACTION_UPDATE);
        getActivity().sendBroadcast(updateWidgetIntent);
        updateTempButtonColor(id);
    }

    private void updateTempButtonColor(int id){
        if(id == R.id.btn_c){
            btnC.setBackgroundColor(activeButtonColor);
            btnF.setBackgroundColor(buttonColor);
            btnScientist.setBackgroundColor(buttonColor);
            btnC.setTextColor(activeButtonTextColor);
            btnF.setTextColor(buttonTextColor);
            btnScientist.setTextColor(buttonTextColor);
        }
        else if(id == R.id.btn_f){
            btnC.setBackgroundColor(buttonColor);
            btnF.setBackgroundColor(activeButtonColor);
            btnScientist.setBackgroundColor(buttonColor);
            btnC.setTextColor(buttonTextColor);
            btnF.setTextColor(activeButtonTextColor);
            btnScientist.setTextColor(buttonTextColor);
        }
        else{
            btnC.setBackgroundColor(buttonColor);
            btnF.setBackgroundColor(buttonColor);
            btnScientist.setBackgroundColor(activeButtonColor);
            btnC.setTextColor(buttonTextColor);
            btnF.setTextColor(buttonTextColor);
            btnScientist.setTextColor(activeButtonTextColor);
        }
    }

    private void changeDistanceUnit(int id){
        String pref = getString(R.string.pref_distance);
        if(id == R.id.btn_km){
            updateSharedPref(pref,getString(R.string.km));
        }
        else if(id == R.id.btn_mi){
            updateSharedPref(pref,getString(R.string.mi));
        }
        else{
            updateSharedPref(pref,getString(R.string.bananas));
        }

        updateDistanceButtonColor(id);
    }

    private void updateDistanceButtonColor(int id){
        if(id == R.id.btn_km){
            btnKm.setBackgroundColor(activeButtonColor);
            btnMi.setBackgroundColor(buttonColor);
            btnBanana.setBackgroundColor(buttonColor);
            btnKm.setTextColor(activeButtonTextColor);
            btnMi.setTextColor(buttonTextColor);
            btnBanana.setTextColor(buttonTextColor);
        }
        else if(id == R.id.btn_mi){
            btnKm.setBackgroundColor(buttonColor);
            btnMi.setBackgroundColor(activeButtonColor);
            btnBanana.setBackgroundColor(buttonColor);
            btnKm.setTextColor(buttonTextColor);
            btnMi.setTextColor(activeButtonTextColor);
            btnBanana.setTextColor(buttonTextColor);
        }
        else{
            btnKm.setBackgroundColor(buttonColor);
            btnMi.setBackgroundColor(buttonColor);
            btnBanana.setBackgroundColor(activeButtonColor);
            btnKm.setTextColor(buttonTextColor);
            btnMi.setTextColor(buttonTextColor);
            btnBanana.setTextColor(activeButtonTextColor);
        }
    }

    private void changeSpeedUnit(int id){
        String pref = getString(R.string.pref_speed);
        if(id == R.id.btn_kmph){
            updateSharedPref(pref,getString(R.string.kmph));
        }
        else if(id == R.id.btn_miph){
            updateSharedPref(pref,getString(R.string.miph));
        }
        else{
            updateSharedPref(pref,getString(R.string.banana_h));
        }
        updateSpeedButtonColor(id);
    }

    private void updateSpeedButtonColor(int id){
        if(id == R.id.btn_kmph){
            btnKmph.setBackgroundColor(activeButtonColor);
            btnMiph.setBackgroundColor(buttonColor);
            btnBananaph.setBackgroundColor(buttonColor);
            btnKmph.setTextColor(activeButtonTextColor);
            btnMiph.setTextColor(buttonTextColor);
            btnBananaph.setTextColor(buttonTextColor);
        }
        else if(id == R.id.btn_miph){
            btnKmph.setBackgroundColor(buttonColor);
            btnMiph.setBackgroundColor(activeButtonColor);
            btnBananaph.setBackgroundColor(buttonColor);
            btnKmph.setTextColor(buttonTextColor);
            btnMiph.setTextColor(activeButtonTextColor);
            btnBananaph.setTextColor(buttonTextColor);
        }
        else{
            btnKmph.setBackgroundColor(buttonColor);
            btnMiph.setBackgroundColor(buttonColor);
            btnBananaph.setBackgroundColor(activeButtonColor);
            btnKmph.setTextColor(buttonTextColor);
            btnMiph.setTextColor(buttonTextColor);
            btnBananaph.setTextColor(activeButtonTextColor);
        }
    }

    private void updateHelpButtonColor(){
        btnHelpDev.setBackgroundColor(buttonColor);
        btnHelpMe.setBackgroundColor(buttonColor);
        btnHelpMe.setTextColor(buttonTextColor);
        btnHelpDev.setTextColor(buttonTextColor);
    }

    private void changePressureUnit(int id){
        String pref = getString(R.string.pref_pressure);
        if(id == R.id.btn_psi){
            updateSharedPref(pref,getString(R.string.psi));
        }
        else if(id == R.id.btn_mmhg){
            updateSharedPref(pref,getString(R.string.mmhg));
        }
        else{
            updateSharedPref(pref,getString(R.string.depression_unit));
        }
        updatePressureButtonColor(id);
    }

    private void updatePressureButtonColor(int id){
        if(id == R.id.btn_psi){
            btnPsi.setBackgroundColor(activeButtonColor);
            btnMmhg.setBackgroundColor(buttonColor);
            btnDepress.setBackgroundColor(buttonColor);
            btnPsi.setTextColor(activeButtonTextColor);
            btnMmhg.setTextColor(buttonTextColor);
            btnDepress.setTextColor(buttonTextColor);
        }
        else if(id == R.id.btn_mmhg){
            btnPsi.setBackgroundColor(buttonColor);
            btnMmhg.setBackgroundColor(activeButtonColor);
            btnDepress.setBackgroundColor(buttonColor);
            btnPsi.setTextColor(buttonTextColor);
            btnMmhg.setTextColor(activeButtonTextColor);
            btnDepress.setTextColor(buttonTextColor);
        }
        else{
            btnPsi.setBackgroundColor(buttonColor);
            btnMmhg.setBackgroundColor(buttonColor);
            btnDepress.setBackgroundColor(activeButtonColor);
            btnPsi.setTextColor(buttonTextColor);
            btnMmhg.setTextColor(buttonTextColor);
            btnDepress.setTextColor(activeButtonTextColor);
        }
    }

    private void updateSharedPref(String pref, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(pref,value);
        editor.apply();
        DetailsFragment.getInstance().updateUnit();
    }

}
