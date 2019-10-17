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
    private Context mContext;
    private Weather weather;


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
        boolean isExplicit = PreferencesUtil.isExplicit(mContext);
        List<Quote> weatherQuotes = filterQuotes(weather, quotes, isExplicit,false);
        Quote randomQuote = weatherQuotes.get(new Random().nextInt(weatherQuotes.size()));
        if (randomQuote.getMain() == null && randomQuote.getSub() == null) {
            randomQuote.setDefaultQuote();
        }
        HomeScreenFragment.getInstance().updateQuote(randomQuote);
    }

    public static List<Quote> filterQuotes(Weather weather,List<Quote> allQuotes, boolean isExplicit, boolean forWidget) {
        List<Quote> weatherQuotes = new ArrayList<>();
        float temp = UnitConverter.toCelsius(weather.getCurrently().getApparentTemperature());
        String summary = weather.getCurrently().getIcon();
        List<String> criteria = new ArrayList<>();
        if (temp > 30) {
            criteria.add("hot");
        } else if (temp < 15) {
            criteria.add("cold");
        } else {
            criteria.add("cool");
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
            criteria.add("clear");
        }
        for (Quote q : allQuotes) {
            // att * means quotes unrelated to weather (jokes / inspiring quotes,...)
            if (q.getAtt().contains("*")) {
                if(!forWidget && q.getAtt().contains("widget")){
                    continue;
                } else {
                    if (isExplicit) {
                        q.setMain(censorOffensiveWords(q.getMain()));
                        q.setSub(censorOffensiveWords(q.getSub()));
                    }
                    weatherQuotes.add(q);
                }
                continue;
            }
            // Quotes related to weather
            for (String s : criteria) {
                //if match one of the criteria
                if (q.getAtt().contains(s)) {
                    if (isExplicit) {
                        q.setMain(censorOffensiveWords(q.getMain()));
                        q.setSub(censorOffensiveWords(q.getSub()));
                    }
                    weatherQuotes.add(q);
                    break;
                }
            }
        }
        return weatherQuotes;
    }

    private static String censorOffensiveWords(String text) {
        String[] alternativesForFucking = {"frickin’ ","freakin’ ","freaking ","flippin’ ","flipping ","fricking ", ""};
        String[] alternativesForDamn = {"darn","dang"};
        String[] alternativesForShit = {"crap","crud"};
        String[] alternativesForHell = {"heck"};
        String[] alternativesForAss = {"arse ","butt ", "bum "};
        text = text.toLowerCase().replace("damn", alternativesForDamn[new Random().nextInt(alternativesForDamn.length)]);
        text = text.replace("shit", alternativesForShit[new Random().nextInt(alternativesForShit.length)]);
        text = text.replace("hell", alternativesForHell[new Random().nextInt(alternativesForHell.length)]);
        text = text.replace("ass ", alternativesForAss[new Random().nextInt(alternativesForAss.length)]);
        text = text.replace("fucking ", alternativesForFucking[new Random().nextInt(alternativesForFucking.length)]);
        String textNoStrongWords = text.replace("i ", "I ").trim();
        if (textNoStrongWords.length() > 0) {
            return capitalizeSentence(textNoStrongWords);
        }
        return text;
    }

    private static String capitalizeSentence(String sentence) {
        StringBuilder result = new StringBuilder();
        boolean capitalize = true;
        for(char c : sentence.toCharArray()) {
            if (capitalize) {
                result.append(Character.toUpperCase(c));
                if (!Character.isWhitespace(c) && c != '.') {
                    capitalize = false;
                }
            } else {
                result.append(c);
                if (c == '.') {
                    capitalize = true;
                }
            }
        }
        return result.toString();
    }

}
