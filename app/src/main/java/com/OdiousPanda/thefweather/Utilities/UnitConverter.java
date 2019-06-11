package com.OdiousPanda.thefweather.Utilities;

public class UnitConverter {

    final static String DEGREE  = "\u00b0";

    private static float toCelsius(float temp){
        return (temp - 32)* 5/9;
    }

    private static float toFahrenheit(float temp){
        return temp;
    }

    private static String toCelsiusPretty(float temp){
        return Math.round(toCelsius(temp)) + DEGREE + "C";
    }

    private static String toFahrenheitPretty(float temp){
        return Math.round(toFahrenheit(temp)) + DEGREE + "F";
    }
    private static float toKelvin(float temp){
        return toCelsius(temp) + 273;
    }

    private static String toKelvinPretty(float temp){
        return Math.round(toKelvin(temp)) + DEGREE + "K";
    }

    public static String convertToUnit(float temp, String unit){
        if(unit.equals(DEGREE + "C")){
            return toCelsiusPretty(temp);
        }
        else if(unit.equals(DEGREE + "F")){
            return toFahrenheitPretty(temp);
        }
        return toKelvinPretty(temp);
    }
}
