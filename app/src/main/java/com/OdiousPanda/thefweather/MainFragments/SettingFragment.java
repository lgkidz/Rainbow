package com.OdiousPanda.thefweather.MainFragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.OdiousPanda.thefweather.R;


public class SettingFragment extends Fragment {

    public static SettingFragment instance;
    private TextView tv_setting_title;
    private Button btn_f;
    private Button btn_c;
    private String currentTempUnit;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        tv_setting_title = v.findViewById(R.id.tv_temp_setting_title);
        btn_c = v.findViewById(R.id.btn_temp_c);
        btn_f = v.findViewById(R.id.btn_temp_f);

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key_string), Context.MODE_PRIVATE);
        currentTempUnit = sharedPreferences.getString(getString(R.string.temp_unit_setting),null);
        if(currentTempUnit == null){
            currentTempUnit = getString(R.string.temp_unit_c);
        }
        setCurrentTempUnit();


        btn_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTemperatureUnit(v);
            }
        });

        btn_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTemperatureUnit(v);
            }
        });


        return v;
    }

    void setCurrentTempUnit(){
        if(currentTempUnit.equals(getString(R.string.temp_unit_c))){
            btn_c.setBackgroundColor(Color.CYAN);
            btn_c.setTextColor(Color.WHITE);
            btn_f.setBackgroundColor(Color.LTGRAY);
            btn_f.setTextColor(Color.GRAY);
        }
        else if(currentTempUnit.equals(getString(R.string.temp_unit_f))){
            btn_f.setBackgroundColor(Color.CYAN);
            btn_f.setTextColor(Color.WHITE);
            btn_c.setBackgroundColor(Color.LTGRAY);
            btn_c.setTextColor(Color.GRAY);
        }
    }

    void changeTemperatureUnit(View v){
        if(v.getId() == R.id.btn_temp_c){
            btn_c.setBackgroundColor(Color.CYAN);
            btn_c.setTextColor(Color.WHITE);
            btn_f.setBackgroundColor(Color.LTGRAY);
            btn_f.setTextColor(Color.GRAY);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.temp_unit_setting),getString(R.string.temp_unit_c));
            editor.apply();
            //HomeScreenFragment.getInstance().updateTempUnit(getString(R.string.temp_unit_c));
            ForecastFragment.getInstance().updateTempUnit(getString(R.string.temp_unit_c));
        }
        else if(v.getId() == R.id.btn_temp_f){
            btn_f.setBackgroundColor(Color.CYAN);
            btn_f.setTextColor(Color.WHITE);
            btn_c.setBackgroundColor(Color.LTGRAY);
            btn_c.setTextColor(Color.GRAY);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.temp_unit_setting),getString(R.string.temp_unit_f));
            editor.apply();
            //HomeScreenFragment.getInstance().updateTempUnit(getString(R.string.temp_unit_f));
            ForecastFragment.getInstance().updateTempUnit(getString(R.string.temp_unit_f));
        }
    }


}
