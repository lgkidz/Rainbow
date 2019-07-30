package com.OdiousPanda.rainbow.DataModel;


import com.OdiousPanda.rainbow.DataModel.AQI.AirQuality;
import com.OdiousPanda.rainbow.DataModel.Weather.Weather;

public class LocationData {
    private Coordinate coordinate;
    private Weather weather;
    private AirQuality airQuality;

    public LocationData(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public AirQuality getAirQuality() {
        return airQuality;
    }

    public void setAirQuality(AirQuality airQuality) {
        this.airQuality = airQuality;
    }

    public boolean matchIdWith(LocationData obj) {
        try {
            return this.getCoordinate().getId() == obj.getCoordinate().getId();
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
