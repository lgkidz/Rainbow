package com.OdiousPanda.thefweather.Utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.OdiousPanda.thefweather.MainFragments.HomeScreenFragment;
import com.OdiousPanda.thefweather.Model.Quote;
import com.OdiousPanda.thefweather.Model.Weather.Weather;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuoteGenerator {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Quote> quotes = new ArrayList<>();
    private static final String TAG = "WeatherA";
    private Weather weather;

    List<Quote> weatherQuotes = new ArrayList<>();

    private static QuoteGenerator instance;

    public static synchronized QuoteGenerator getInstance() {
        if(instance == null){
            instance = new QuoteGenerator();
        }
        return instance;
    }

    private void doQuery(){
        db.collection("quotes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Quote q = document.toObject(Quote.class);
                                quotes.add(q);
                            }
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        getQuote(weather);
                    }
                });
    }

    public void getQuote(Weather weather){
        this.weather = weather;
        if(quotes.size() == 0){
            doQuery();
            return;
        }

        float temp = UnitConverter.toCelsius(weather.getCurrently().getApparentTemperature());
        String summary = weather.getCurrently().getIcon();
        List<String> criteria = new ArrayList<>();
        if(temp > 30){
            criteria.add("hot");
        }
        else if(temp < 10){
            criteria.add("cold");
        }
        else{
            criteria.add("clear");
        }

        if(summary.contains("rain")){
            criteria.add("rain");
        }
        else if(summary.contains("cloudy")){
            criteria.add("cloudy");
        }
        else if(summary.contains("fog")){
            criteria.add("fog");
        }
        else if(summary.contains("clear")){
            if(!criteria.contains("clear")){
                criteria.add("clear");
            }
        }

        for(Quote q : quotes){
            for(String s: criteria){
                if (q.getAtt().contains(s)){
                    weatherQuotes.add(q);
                    break;
                }
            }
        }

        Quote randomQuote = weatherQuotes.get(new Random().nextInt(weatherQuotes.size()));
        HomeScreenFragment.getInstance().updateQuote(randomQuote);
    }
}
