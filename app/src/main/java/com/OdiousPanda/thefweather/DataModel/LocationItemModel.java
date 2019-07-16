package com.OdiousPanda.thefweather.DataModel;

import com.OdiousPanda.thefweather.DataModel.Weather.Weather;

public class LocationItemModel {
    private int id;
    private String locationName;
    private Weather weather;

    public LocationItemModel(int id,String locationName, Weather weather) {
        this.id = id;
        this.locationName = locationName;
        this.weather = weather;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
