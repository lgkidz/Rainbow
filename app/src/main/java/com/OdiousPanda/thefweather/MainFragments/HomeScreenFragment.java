package com.OdiousPanda.thefweather.MainFragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.OdiousPanda.thefweather.DataModel.Quote;
import com.OdiousPanda.thefweather.DataModel.Weather.Weather;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.PreferencesUtil;
import com.OdiousPanda.thefweather.Utilities.QuoteGenerator;
import com.OdiousPanda.thefweather.Utilities.UnitConverter;

import java.util.Objects;

public class HomeScreenFragment extends Fragment {

    private static HomeScreenFragment instance;

    public static HomeScreenFragment getInstance(){
        if (instance == null){
            instance = new HomeScreenFragment();
        }

        return instance;
    }

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvTemp;
    private TextView tvDescription;
    private TextView tvBigText;
    private TextView tvSmallText;
    private ImageView icon;
    private String iconName;

    private Weather currentWeather;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_screen, container, false);
        initViews(v);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initViews(View v){
        tvBigText = v.findViewById(R.id.big_text);
        tvDescription = v.findViewById(R.id.tv_description);
        tvSmallText = v.findViewById(R.id.small_text);
        tvTemp = v.findViewById(R.id.tv_temp);
        icon = v.findViewById(R.id.icon);
        swipeRefreshLayout = v.findViewById(R.id.home_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callback.updateData();
            }
        });
    }

    public void setColorTheme(int textColor){
        int colorFrom = tvTemp.getCurrentTextColor();
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, textColor);
        colorAnimation.setDuration(200); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                tvTemp.setTextColor((int) animator.getAnimatedValue());
                tvDescription.setTextColor((int) animator.getAnimatedValue());
                tvBigText.setTextColor((int) animator.getAnimatedValue());
                tvSmallText.setTextColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
        if(textColor == Color.WHITE){
            int iconResourceId = Objects.requireNonNull(getActivity()).getResources().getIdentifier("drawable/" + iconName + "_w", null, getActivity().getPackageName());
            icon.setImageResource(iconResourceId);
        }
        else{
            int iconResourceId = Objects.requireNonNull(getActivity()).getResources().getIdentifier("drawable/" + iconName + "_b", null, getActivity().getPackageName());
            icon.setImageResource(iconResourceId);
        }
    }

    void updateUnit(){
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(Objects.requireNonNull(getActivity()));
        tvTemp.setText(UnitConverter.convertToTemperatureUnit(currentWeather.getCurrently().getTemperature(),currentTempUnit));
    }

    public void updateData(Weather weather){
        currentWeather = weather;
        QuoteGenerator.getInstance(getActivity()).getQuote(weather);
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(Objects.requireNonNull(getActivity()));
        tvTemp.setText(UnitConverter.convertToTemperatureUnit(currentWeather.getCurrently().getTemperature(),currentTempUnit));
        tvDescription.setText(currentWeather.getCurrently().getSummary());
        String iconNameRaw = currentWeather.getCurrently().getIcon();
        iconName = iconNameRaw.replace("-", "_");
        swipeRefreshLayout.setRefreshing(false);
    }

    void updateExplicitSetting(){
        QuoteGenerator.getInstance(getActivity()).getQuote(currentWeather);
    }

    private OnLayoutRefreshListener callback;

    public void setOnRefreshListener(OnLayoutRefreshListener callback){
        this.callback = callback;
    }

    public interface OnLayoutRefreshListener{
        void updateData();
    }

    public void updateQuote(Quote quote){
        tvBigText.setText(quote.getMain());
        tvSmallText.setText(quote.getSub());
    }
}
