package com.OdiousPanda.thefweather.Utilities;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.OdiousPanda.thefweather.MainFragments.HomeScreenFragment;
import com.OdiousPanda.thefweather.DataModel.Quote;
import com.OdiousPanda.thefweather.DataModel.Weather.Weather;
import com.OdiousPanda.thefweather.R;
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
    private Context mContext;

    List<Quote> weatherQuotes = new ArrayList<>();

    private static QuoteGenerator instance;

    public static synchronized QuoteGenerator getInstance(Context context) {
        if(instance == null){
            instance = new QuoteGenerator(context);
        }
        return instance;
    }

    public QuoteGenerator(Context context){
        this.mContext = context;
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
        else if(temp < 15){
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
        else if(summary.contains("snow") || summary.contains("sleet")){
            criteria.add("snow");
        }
        else if(summary.contains("hail")){
            criteria.add("hail");
        }
        else if(summary.contains("thunderstorm")){
            criteria.add("thunderstorm");
        }
        else if(summary.contains("tornado")){
            criteria.add("tornado");
        }
        else if(summary.contains("clear")){
            if(!criteria.contains("clear")){
                criteria.add("clear");
            }
        }

        weatherQuotes.clear();

        String explicit = mContext.getSharedPreferences(mContext.getString(R.string.pref_key_string),Context.MODE_PRIVATE).getString(mContext.getString(R.string.pref_explicit),mContext.getString(R.string.im_not));
        for(Quote q : quotes){
            if(q.getAtt().contains("*")){
                if(!q.getAtt().contains("widget")){
                    Quote tempQuote = q;
                    if(explicit.equals(mContext.getString(R.string.im_not))){
                        tempQuote.setMain(censorStrongWords(q.getMain()));
                        tempQuote.setSub(censorStrongWords(q.getSub()));
                    }
                    weatherQuotes.add(tempQuote);
                }
                continue;
            }
            for(String s: criteria){
                if (q.getAtt().contains(s)){
                    Quote tempQuote = q;
                    if(explicit.equals(mContext.getString(R.string.im_not))){
                        tempQuote.setMain(censorStrongWords(q.getMain()));
                        tempQuote.setSub(censorStrongWords(q.getSub()));
                    }
                    weatherQuotes.add(tempQuote);
                    break;
                }
            }
        }

        Quote randomQuote = weatherQuotes.get(new Random().nextInt(weatherQuotes.size()));
        HomeScreenFragment.getInstance().updateQuote(randomQuote);
    }

    private String censorStrongWords(String text){
        String textNoStrongWords = text.toLowerCase().replace("fucking ","").trim();
        if(textNoStrongWords.length() > 0){
            return textNoStrongWords.substring(0, 1).toUpperCase() + textNoStrongWords.substring(1);
        }

        return text;
    }
}
