package com.OdiousPanda.thefweather.MainFragments;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.ColorUtil;

public class HomeScreenFragment extends Fragment {
    private TextView mSearchResultsTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private String currentTempUnit;

    private int currentBackgroundColor = Color.argb(255,255,255,255);


    public static HomeScreenFragment instance;

    public static HomeScreenFragment getInstance(){
        if (instance == null){
            instance = new HomeScreenFragment();
        }

        return instance;
    }

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_screen, container, false);

        mSearchResultsTextView = v.findViewById(R.id.tv_github_search_results_json);
        swipeRefreshLayout = v.findViewById(R.id.home_layout);

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key_string), Context.MODE_PRIVATE);
        currentTempUnit = sharedPreferences.getString(getString(R.string.temp_unit_setting),null);
        if(currentTempUnit == null){
            currentTempUnit = getString(R.string.temp_unit_c);
        }
        setColorTheme();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callback.updateData();
            }
        });

        return v;
    }

    public void setColorTheme(){
        final int[] argb = ColorUtil.randomColorCode();
        int textColorCode = ColorUtil.invertColor(argb);
        int currentTextColor = mSearchResultsTextView.getCurrentTextColor();
        ObjectAnimator textFade = ObjectAnimator.ofObject(mSearchResultsTextView
        ,"textColor"
        ,new ArgbEvaluator()
        ,currentTextColor
        ,textColorCode);
        textFade.setDuration(200);
        ObjectAnimator colorFade = ObjectAnimator.ofObject(swipeRefreshLayout
                ,"backgroundColor"
                , new ArgbEvaluator()
                , currentBackgroundColor
                , Color.argb(argb[0],argb[1],argb[2],argb[3]));
        colorFade.setDuration(200);
        colorFade.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentBackgroundColor = Color.argb(argb[0],argb[1],argb[2],argb[3]);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        colorFade.start();
        textFade.start();
    }


    public void updateData(CurrentWeather currentWeather){
        String text = "";
        text += "city: " + currentWeather.getName() + "\n";
        text += "country code: " +currentWeather.getSys().getCountry() +"\n";
        text += "lat: " + currentWeather.getCoord().getLat() + ", lon: " + currentWeather.getCoord().getLon() +"\n";
        text += "weather: " + currentWeather.getWeather().get(0).getMain() +", " + currentWeather.getWeather().get(0).getDescription() +"\n";
        //text += "temp min: " + TemperatureConverter.convertToUnitPretty(getActivity(),currentWeather.getMain().getTempMin(),unit) + ", temp max: " + TemperatureConverter.convertToUnitPretty(getActivity(),currentWeather.getMain().getTempMax(),unit) + " \n";
        text += "humidity: " + currentWeather.getMain().getHumidity() +"% \n";
        swipeRefreshLayout.setRefreshing(false);
        setColorTheme();
        mSearchResultsTextView.setText(text);
    }

    OnLayoutRefreshListener callback;

    public void setOnTextClickListener(OnLayoutRefreshListener callback){
        this.callback = callback;
    }

    public interface OnLayoutRefreshListener{
        public void updateData();
    }
}
