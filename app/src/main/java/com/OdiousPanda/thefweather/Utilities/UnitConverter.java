package com.OdiousPanda.thefweather.Utilities;

import java.util.Random;

public class UnitConverter {

    private final static String DEGREE  = "\u00b0";

    public static float toCelsius(float temp){
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

    public static String convertToTemperatureUnit(float temp, String unit){
        if(unit.equals(DEGREE + "C")){
            return toCelsiusPretty(temp);
        }
        else if(unit.equals(DEGREE + "F")){
            return toFahrenheitPretty(temp);
        }
        return toKelvinPretty(temp);
    }

    public static String convertToDistanceUnit(float distance, String unit){
        if(unit.equals("km")){
            return Math.round(distance * 1.609) + " km";
        }
        else if(unit.equals("bananas")){
            return Math.round(distance * 9041.254) + " bananas";
        }
        return Math.round(distance) + " mile";
    }

    public static String convertToSpeedUnit(float speed, String unit){
        if(unit.equals("kmph")){
            return Math.round(speed * 1.609) + " km/h";
        }
        else if(unit.equals("bananas per hour")){
            return Math.round(speed * 9041.254) + " banana/h";
        }
        return Math.round(speed) + " mph";
    }

    public static float toMeterPerSecond(float speed){
        return (float)(speed * 0.447);
    }

    public static String convertToPressureUnit(float pressure, String unit){
        if(unit.equals("psi")){
            return Math.round(pressure / 68.948) + " psi";
        }
        else if(unit.equals("mmHg")){
            return Math.round(pressure / 1.333) + " mmHg";
        }
        else{
            String[] depressLevels = {"Mild","Depressed","Crippling","A little","Let's die","Oof","Happy af!","Not at all","Over 9000"};
            return depressLevels[new Random().nextInt(depressLevels.length)];
        }
    }
}
