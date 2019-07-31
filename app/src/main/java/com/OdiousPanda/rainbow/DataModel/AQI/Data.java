package com.OdiousPanda.rainbow.DataModel.AQI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("aqi")
    @Expose
    public Float aqi;
    @SerializedName("idx")
    @Expose
    public Integer idx;
    @SerializedName("attributions")
    @Expose
    public List<Attribution> attributions = null;
    @SerializedName("city")
    @Expose
    public City city;
    @SerializedName("dominentpol")
    @Expose
    public String dominentpol;
    @SerializedName("iaqi")
    @Expose
    public Iaqi iaqi;
    @SerializedName("time")
    @Expose
    public Time time;
    @SerializedName("debug")
    @Expose
    public Debug debug;

}
