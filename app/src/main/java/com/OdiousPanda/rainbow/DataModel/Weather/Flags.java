package com.OdiousPanda.rainbow.DataModel.Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Flags {
    @SerializedName("sources")
    @Expose
    private List<String> sources = null;
    @SerializedName("nearest-station")
    @Expose
    private Float nearestStation;
    @SerializedName("units")
    @Expose
    private String units;

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
