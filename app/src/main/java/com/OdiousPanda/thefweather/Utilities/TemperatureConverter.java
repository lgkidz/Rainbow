package com.OdiousPanda.thefweather.Utilities;

import android.content.Context;

import com.OdiousPanda.thefweather.R;

public class TemperatureConverter {

    final static String DEGREE  = "\u00b0";

    private static double toCelsius(Double temp){
        return temp - 273;
    }

    private static double toFahrenheit(Double temp){
        return (((temp - 273) * 9/5) + 32);
    }

    private static String toCelsiusPretty(Double temp){
        return Math.round(toCelsius(temp)) + DEGREE + "C";
    }

    private static String toFahrenheitPretty(Double temp){
        return Math.round(toFahrenheit(temp)) + DEGREE + "F";
    }

    private static String toKelvinPretty(Double temp){
        return Math.round(temp) + DEGREE + "K";
    }

    public static String convertToUnit(Context context,Double temp, String unit){
        if(unit.equals(context.getString(R.string.temp_setting_degree_c))){
            return toCelsiusPretty(temp);
        }
        else if(unit.equals(context.getString(R.string.temp_setting_degree_f))){
            return toFahrenheitPretty(temp);
        }
        return toKelvinPretty(temp);
    }
}
