package com.OdiousPanda.thefweather.Model.AQI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Time {

    @SerializedName("s")
    @Expose
    public String s;
    @SerializedName("tz")
    @Expose
    public String tz;
    @SerializedName("v")
    @Expose
    public Integer v;

}
