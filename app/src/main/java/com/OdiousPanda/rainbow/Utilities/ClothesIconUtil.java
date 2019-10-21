package com.OdiousPanda.rainbow.Utilities;

import android.content.Context;

import com.OdiousPanda.rainbow.DataModel.Weather.Weather;
import com.OdiousPanda.rainbow.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClothesIconUtil {
    private Weather weather;
    private String criteria = "cool";
    private boolean rain = false;
    private List<Integer> head = new ArrayList<>();
    private List<Integer> upper = new ArrayList<>();
    private List<Integer> lower = new ArrayList<>();
    private List<Integer> foot = new ArrayList<>();
    private List<Integer> hand = new ArrayList<>();

    public ClothesIconUtil(Weather weather) {
        this.weather = weather;
        generateCriteria();
        getIcons();
    }

    private void getIcons() {
        Field[] drawablesFields = com.OdiousPanda.rainbow.R.drawable.class.getFields();
        for (Field field : drawablesFields) {
            try {
                if (field.getName().contains("head_")) {
                    if (field.getName().contains(criteria)) {
                        head.add(field.getInt(null));
                    }
                } else if (field.getName().contains("upper_")) {
                    if (field.getName().contains(criteria)) {
                        upper.add(field.getInt(null));
                    }
                } else if (field.getName().contains("lower_")) {
                    if (field.getName().contains(criteria)) {
                        lower.add(field.getInt(null));
                    }
                } else if (field.getName().contains("foot_")) {
                    if (field.getName().contains(criteria)) {
                        foot.add(field.getInt(null));
                    }
                } else if (field.getName().contains("hand_")) {
                    hand.add(field.getInt(null));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void generateCriteria() {
        float temp = UnitConverter.toCelsius(weather.getCurrently().getApparentTemperature());
        String summary = weather.getCurrently().getIcon();
        if (temp > 30) {
            criteria = "hot";
        } else if (temp < 15) {
            criteria = "cold";
        } else {
            criteria = "cool";
        }
        if (summary.contains("rain")) {
            rain = true;
        }
    }

    public String getCause(Context context) {
        String criteriaString = context.getString(R.string.cool);
        if (criteria.equals("hot")) {
            criteriaString = context.getString(R.string.hot);
        } else if (criteria.equals("cold")) {
            criteriaString = context.getString(R.string.cold);
        }
        return rain ? " " + criteriaString + " " + context.getString(R.string.and_raining) : " " + criteriaString + ".";
    }

    public int getHeadIcon() {
        return head.get(new Random().nextInt(head.size()));
    }

    public int getUpperIcon() {
        return upper.get(new Random().nextInt(upper.size()));
    }

    public int getLowerIcon() {
        return lower.get(new Random().nextInt(lower.size()));
    }

    public int getFootIcon() {
        return foot.get(new Random().nextInt(foot.size()));
    }

    public int getHandIcon() {
        if (rain) {
            return R.drawable.rain_ic_umbrella;
        }
        return hand.get(new Random().nextInt(hand.size()));
    }
}
