package com.odiousPanda.rainbowKt.utilities

import android.content.Context
import com.google.gson.Gson
import com.odiousPanda.rainbowKt.model.dataSource.Coordinate
import java.util.*

object PreferencesUtil {
    const val TEMPERATURE_UNIT = "tempUnit"
    const val DISTANCE_UNIT = "distanceUnit"
    const val SPEED_UNIT = "speedUnit"
    const val PRESSURE_UNIT = "pressureUnit"
    private const val EXPLICIT_SETTING = "explicitOrNot"
    const val BACKGROUND_COLOR = "color"
    const val BACKGROUND_PICTURE = "picture"
    const val BACKGROUND_PICTURE_RANDOM = "picture_random"
    const val NOTIFICATION_SETTING_ON = "on"
    const val NOTIFICATION_SETTING_OFF = "off"
    private const val BACKGROUND_SETTING = "backgroundType"
    private const val LANGUAGE_SETTING = "language"
    private const val CURRENT_LOCATION = "currentLocation"

    // Shared preferences file name
    private const val PREF_NAME = "RainbowPreferencesName"
    private const val WIDGET_ACTION_TAP = "widgetTap"
    private const val DEFAULT_TEMPERATURE_UNIT = "°C"
    private const val DEFAULT_DISTANCE_UNIT = "km"
    private const val DEFAULT_SPEED_UNIT = "kmph"
    private const val DEFAULT_PRESSURE_UNIT = "psi"
    private const val DEFAULT_BACKGROUND_SETTING = BACKGROUND_COLOR
    private const val DEFAULT_EXPLICIT_SETTING = true
    private const val NOTIFICATION_SETTING = "notification"
    private const val NOTIFICATION_TIME = "notificationTime"
    private const val NOTIFICATION_TIME_DEFAULT = "8:00"
    private const val APP_OPEN_COUNT = "App_open_count"
    private const val DEFAULT_LANGUAGE = "en"

    fun getCurrentLocation(context: Context): Coordinate {
        val locationJson = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(CURRENT_LOCATION, "")
        return Gson().fromJson(locationJson, Coordinate::class.java) ?: Coordinate()
    }

    fun setCurrentLocation(context: Context, coordinate: Coordinate) {
        val locationJson = Gson().toJson(coordinate)
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putString(CURRENT_LOCATION, locationJson).apply()
    }

    fun getAppOpenCount(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(APP_OPEN_COUNT, 0)
    }

    fun increaseAppOpenCount(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putInt(APP_OPEN_COUNT, getAppOpenCount(context) + 1).apply()
    }

    fun getNotificationTime(context: Context): Calendar {
        val calendar = Calendar.getInstance().also { it.timeInMillis = System.currentTimeMillis() }
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(NOTIFICATION_TIME, NOTIFICATION_TIME_DEFAULT)?.split(":")?.toTypedArray()
            ?.let {
                calendar[Calendar.HOUR_OF_DAY] = it[0].toInt()
                calendar[Calendar.MINUTE] = it[1].toInt()
            }
        return calendar
    }

    fun setNotificationTime(context: Context, hour: Int, minute: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putString(NOTIFICATION_TIME, "$hour:$minute").apply()
    }

    fun getNotificationSetting(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(NOTIFICATION_SETTING, NOTIFICATION_SETTING_ON)
    }

    fun setNotificationSetting(context: Context, value: String?) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putString(NOTIFICATION_SETTING, value).apply()

    }

    fun getBackgroundSetting(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(BACKGROUND_SETTING, DEFAULT_BACKGROUND_SETTING)
    }

    fun setBackgroundSetting(context: Context, value: String?) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putString(BACKGROUND_SETTING, value).apply()
    }

    fun getTemperatureUnit(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(TEMPERATURE_UNIT, DEFAULT_TEMPERATURE_UNIT)
    }

    fun getDistanceUnit(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(DISTANCE_UNIT, DEFAULT_DISTANCE_UNIT)
    }

    fun getSpeedUnit(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(SPEED_UNIT, DEFAULT_SPEED_UNIT)
    }

    fun getPressureUnit(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(PRESSURE_UNIT, DEFAULT_PRESSURE_UNIT)
    }

    fun isExplicit(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(EXPLICIT_SETTING, DEFAULT_EXPLICIT_SETTING)
    }

    fun setUnitSetting(context: Context, preferenceName: String?, value: String?) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putString(preferenceName, value).apply()
    }

    fun setExplicitSetting(context: Context, value: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(EXPLICIT_SETTING, value).apply()
    }

    fun getWidgetTapCount(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(WIDGET_ACTION_TAP, 0)
    }

    fun setWidgetTapCount(context: Context, count: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putInt(WIDGET_ACTION_TAP, count).apply()
    }
}