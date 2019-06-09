package com.OdiousPanda.thefweather.MainFragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private TextView tvTemp;
    private TextView tvDescription;
    private TextView tvBigText;
    private TextView tvSmallText;
    private ImageView icon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_screen, container, false);

        initViews(v);

        return v;
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
        int colorTo = textColor;
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
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
            int iconResourceId = getActivity().getResources().getIdentifier("drawable/" + "ic_01d_w", null, getActivity().getPackageName());
            icon.setImageResource(iconResourceId);
        }
        else{
            int iconResourceId = getActivity().getResources().getIdentifier("drawable/" + "ic_01d_b", null, getActivity().getPackageName());
            icon.setImageResource(iconResourceId);
        }


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
