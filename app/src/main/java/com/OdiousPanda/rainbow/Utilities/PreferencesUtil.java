package com.OdiousPanda.rainbow.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Objects;

public class PreferencesUtil {
    public static final String TEMPERATURE_UNIT = "tempUnit";
    public static final String DISTANCE_UNIT = "distanceUnit";
    public static final String SPEED_UNIT = "speedUnit";
    public static final String PRESSURE_UNIT = "pressureUnit";
    public static final String EXPLICIT_SETTING = "explicitOrNot";
    public static final String BACKGROUND_COLOR = "color";
    public static final String BACKGROUND_PICTURE = "picture";
    public static final String BACKGROUND_PICTURE_RANDOM = "picture_random";
    public static final String NOTIFICATION_SETTING_ON = "on";
    public static final String NOTIFICATION_SETTING_OFF = "off";
    private static final String BACKGROUND_SETTING = "backgroundType";
    // Shared preferences file name
    private static final String PREF_NAME = "RainbowPreferencesName";
    private static final String WIDGET_ACTION_TAP = "widgetTap";
    private static final String DEFAULT_TEMPERATURE_UNIT = "°C";
    private static final String DEFAULT_DISTANCE_UNIT = "km";
    private static final String DEFAULT_SPEED_UNIT = "kmph";
    private static final String DEFAULT_PRESSURE_UNIT = "psi";
    private static final String DEFAULT_BACKGROUND_SETTING = BACKGROUND_COLOR;
    private static final boolean DEFAULT_EXPLICIT_SETTING = true;
    private static final String NOTIFICATION_SETTING = "notification";
    private static final String NOTIFICATION_TIME = "notificationTime";
    private static final String NOTIFICATION_TIME_DEFAULT = "8:00";
    private static final String APP_OPEN_COUNT = "App_open_count";

    public static int getAppOpenCount(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(APP_OPEN_COUNT, 0);
    }

    public static void increaseAppOpenCount(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(APP_OPEN_COUNT, (getAppOpenCount(context) + 1));
        editor.apply();
    }

    public static Calendar getNotificationTime(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String[] parts = Objects.requireNonNull(pref.getString(NOTIFICATION_TIME, NOTIFICATION_TIME_DEFAULT)).split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        return calendar;
    }

    public static void setNotificationTime(Context context, int hourInt, int minuteInt) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String hour = String.valueOf(hourInt);
        String minute = String.valueOf(minuteInt);
        String time = hour + ":" + minute;
        editor.putString(NOTIFICATION_TIME, time);
        editor.apply();
    }

    public static String getNotificationSetting(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(NOTIFICATION_SETTING, NOTIFICATION_SETTING_ON);
    }

    public static void setNotificationSetting(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(NOTIFICATION_SETTING, value);
        editor.apply();
    }

    public static String getBackgroundSetting(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(BACKGROUND_SETTING, DEFAULT_BACKGROUND_SETTING);
    }

    public static void setBackgroundSetting(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(BACKGROUND_SETTING, value);
        editor.apply();
    }

    public static String getTemperatureUnit(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(TEMPERATURE_UNIT, DEFAULT_TEMPERATURE_UNIT);
    }

    public static String getDistanceUnit(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(DISTANCE_UNIT, DEFAULT_DISTANCE_UNIT);
    }

    public static String getSpeedUnit(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(SPEED_UNIT, DEFAULT_SPEED_UNIT);
    }

    public static String getPressureUnit(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(PRESSURE_UNIT, DEFAULT_PRESSURE_UNIT);
    }

    public static boolean isExplicit(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(EXPLICIT_SETTING, DEFAULT_EXPLICIT_SETTING);
    }

    public static void setUnitSetting(Context context, String preferenceName, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(preferenceName, value);
        editor.apply();
    }

    public static void setExplicitSetting(Context context, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(EXPLICIT_SETTING, value);
        editor.apply();
    }

    public static int getWidgetTapCount(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(WIDGET_ACTION_TAP, 0);
    }

    public static void setWidgetTapCount(Context context, int count) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(WIDGET_ACTION_TAP, count);
        editor.apply();
    }
}
