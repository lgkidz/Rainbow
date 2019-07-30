package com.OdiousPanda.rainbow.Utilities;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.OdiousPanda.rainbow.DataModel.Quote;
import com.OdiousPanda.rainbow.DataModel.Weather.Weather;
import com.OdiousPanda.rainbow.MainFragments.HomeScreenFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class QuoteGenerator {
    private static final String TAG = "WeatherA";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Quote> quotes = new ArrayList<>();
    private Weather weather;
    private Context mContext;

    private List<Quote> weatherQuotes = new ArrayList<>();

    public QuoteGenerator(Context context) {
        this.mContext = context;
    }

    private void queryQuotes() {
        db.collection("quotes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Quote q = document.toObject(Quote.class);
                                quotes.add(q);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        updateHomeScreenQuote(weather);
                    }
                });
    }

    public void updateHomeScreenQuote(Weather weather) {
        this.weather = weather;
        if (quotes.size() == 0) {
            queryQuotes();
            return;
        }
        filterQuotes();
        Quote randomQuote = weatherQuotes.get(new Random().nextInt(weatherQuotes.size()));
        if(randomQuote.getMain() == null && randomQuote.getSub() == null){
            randomQuote.setDefaultQuote();
        }
        HomeScreenFragment.getInstance().updateQuote(randomQuote);
    }

    private void filterQuotes(){
        float temp = UnitConverter.toCelsius(weather.getCurrently().getApparentTemperature());
        String summary = weather.getCurrently().getIcon();
        List<String> criteria = new ArrayList<>();
        weatherQuotes.clear();
        if (temp > 30) {
            criteria.add("hot");
        } else if (temp < 15) {
            criteria.add("cold");
        } else {
            criteria.add("clear");
        }
        if (summary.contains("rain")) {
            criteria.add("rain");
        } else if (summary.contains("cloudy")) {
            criteria.add("cloudy");
        } else if (summary.contains("fog")) {
            criteria.add("fog");
        } else if (summary.contains("snow") || summary.contains("sleet")) {
            criteria.add("snow");
        } else if (summary.contains("hail")) {
            criteria.add("hail");
        } else if (summary.contains("thunderstorm")) {
            criteria.add("thunderstorm");
        } else if (summary.contains("tornado")) {
            criteria.add("tornado");
        } else if (summary.contains("clear")) {
            if (!criteria.contains("clear")) {
                criteria.add("clear");
            }
        }

        boolean isExplicit = PreferencesUtil.isExplicit(mContext);
        for (Quote q : quotes) {
            if (q.getAtt().contains("*")) {
                if (!q.getAtt().contains("widget")) {
                    if (isExplicit) {
                        q.setMain(censorStrongWords(q.getMain()));
                        q.setSub(censorStrongWords(q.getSub()));
                    }
                    weatherQuotes.add(q);
                }
                continue;
            }
            for (String s : criteria) {
                if (q.getAtt().contains(s)) {
                    if (isExplicit) {
                        q.setMain(censorStrongWords(q.getMain()));
                        q.setSub(censorStrongWords(q.getSub()));
                    }
                    weatherQuotes.add(q);
                    break;
                }
            }
        }
    }

    private String censorStrongWords(String text) {
        String textNoStrongWords = text.toLowerCase().replace("fucking ", "").trim();
        if (textNoStrongWords.length() > 0) {
            return textNoStrongWords.substring(0, 1).toUpperCase() + textNoStrongWords.substring(1);
        }
        return text;
    }
}
