package com.OdiousPanda.thefweather.MainFragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.OdiousPanda.thefweather.Model.CurrentWeather.CurrentWeather;
import com.OdiousPanda.thefweather.R;

public class HomeScreenFragment extends Fragment {

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

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_screen, container, false);

        initViews(v);

        return v;
    }

    private void initViews(View v){
        swipeRefreshLayout = v.findViewById(R.id.home_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callback.updateData();
            }
        });
    }

    public void setColorTheme(int textColor){
//        int currentTextColor = mSearchResultsTextView.getCurrentTextColor();
//        ObjectAnimator textFade = ObjectAnimator.ofObject(mSearchResultsTextView
//        ,"textColor"
//        ,new ArgbEvaluator()
//        ,currentTextColor
//        ,textColor);
//        textFade.setDuration(200);
//        textFade.start();
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

    }

    OnLayoutRefreshListener callback;

    public void setOnTextClickListener(OnLayoutRefreshListener callback){
        this.callback = callback;
    }

    public interface OnLayoutRefreshListener{
        void updateData();
    }
}
