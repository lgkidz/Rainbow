package com.OdiousPanda.thefweather.Utilities;

import android.content.Context;

import com.OdiousPanda.thefweather.R;

public class TemperatureConverter {

    final static String DEGREE  = "\u00b0";

    public static double toCelsius(Double temp){
        return temp - 273;
    }

    public static double toFahrenheit(Double temp){
        return (((temp - 273) * 9/5) + 32);
    }

    public static String toCelsiusPretty(Double temp){
        return String.valueOf(Math.round(toCelsius(temp))) + DEGREE + "C";
    }

    public static String toFahrenheitPretty(Double temp){
        return String.valueOf(Math.round(toFahrenheit(temp))) + DEGREE + "F";
    }

    public static double convertToUnit(Context context,Double temp, String unit){
        if(unit.equals(context.getString(R.string.temp_unit_c))){
            return toCelsius(temp);
        }
        else if(unit.equals(context.getString(R.string.temp_unit_f))){
            return toFahrenheit(temp);
        }
        return toCelsius(temp);
    }

    public static String convertToUnitPretty(Context context,Double temp, String unit){
        if(unit.equals(context.getString(R.string.temp_unit_c))){
            return toCelsiusPretty(temp);
        }
        else if(unit.equals(context.getString(R.string.temp_unit_f))){
            return toFahrenheitPretty(temp);
        }
        return toCelsiusPretty(temp);
    }
}
