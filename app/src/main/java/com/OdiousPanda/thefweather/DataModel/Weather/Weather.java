package com.OdiousPanda.thefweather.DataModel.Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("latitude")
    @Expose
    public Float latitude;
    @SerializedName("longitude")
    @Expose
    public Float longitude;
    @SerializedName("timezone")
    @Expose
    public String timezone;
    @SerializedName("currently")
    @Expose
    public Currently currently;
    @SerializedName("hourly")
    @Expose
    public Hourly hourly;
    @SerializedName("daily")
    @Expose
    public Daily daily;
    @SerializedName("flags")
    @Expose
    public Flags flags;
    @SerializedName("offset")
    @Expose
    public Integer offset;

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Currently getCurrently() {
        return currently;
    }

    public void setCurrently(Currently currently) {
        this.currently = currently;
    }

    public Hourly getHourly() {
        return hourly;
    }

    public void setHourly(Hourly hourly) {
        this.hourly = hourly;
    }

    public Daily getDaily() {
        return daily;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
