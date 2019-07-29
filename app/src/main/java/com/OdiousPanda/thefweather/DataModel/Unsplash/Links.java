package com.OdiousPanda.thefweather.DataModel.Unsplash;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    @SerializedName("self")
    @Expose
    public String self;
    @SerializedName("html")
    @Expose
    public String html;
    @SerializedName("download")
    @Expose
    public String download;
    @SerializedName("download_location")
    @Expose
    public String downloadLocation;

}
