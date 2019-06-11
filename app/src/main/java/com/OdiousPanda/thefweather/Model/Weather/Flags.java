package com.OdiousPanda.thefweather.Model.Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Flags {
    @SerializedName("sources")
    @Expose
    public List<String> sources = null;
    @SerializedName("nearest-station")
    @Expose
    public Float nearestStation;
    @SerializedName("units")
    @Expose
    public String units;

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public Float getNearestStation() {
        return nearestStation;
    }

    public void setNearestStation(Float nearestStation) {
        this.nearestStation = nearestStation;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
