package com.OdiousPanda.thefweather.Model.AQI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Iaqi {

    @SerializedName("co")
    @Expose
    public Co co;
    @SerializedName("h")
    @Expose
    public H h;
    @SerializedName("no2")
    @Expose
    public No2 no2;
    @SerializedName("o3")
    @Expose
    public O3 o3;
    @SerializedName("p")
    @Expose
    public P p;
    @SerializedName("pm10")
    @Expose
    public Pm10 pm10;
    @SerializedName("pm25")
    @Expose
    public Pm25 pm25;
    @SerializedName("so2")
    @Expose
    public So2 so2;
    @SerializedName("w")
    @Expose
    public W w;

}
