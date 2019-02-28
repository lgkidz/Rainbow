package com.OdiousPanda.thefweather.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SavedCoord {
    @SerializedName("lon")
    @Expose
    private double lon;
    @SerializedName("lat")
    @Expose
    private double lat;

    public SavedCoord(){}

    public SavedCoord(double lat, double lon){
        this.lat = round(lat,2);
        this.lon = round(lon,2);
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = round(lon,2);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = round(lat,2);
    }

    public  double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
