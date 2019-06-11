package com.OdiousPanda.thefweather.Model.Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum_ {
    @SerializedName("time")
    @Expose
    public Integer time;
    @SerializedName("summary")
    @Expose
    public String summary;
    @SerializedName("icon")
    @Expose
    public String icon;
    @SerializedName("sunriseTime")
    @Expose
    public Integer sunriseTime;
    @SerializedName("sunsetTime")
    @Expose
    public Integer sunsetTime;
    @SerializedName("moonPhase")
    @Expose
    public Float moonPhase;
    @SerializedName("precipIntensity")
    @Expose
    public Float precipIntensity;
    @SerializedName("precipIntensityMax")
    @Expose
    public Float precipIntensityMax;
    @SerializedName("precipIntensityMaxTime")
    @Expose
    public Integer precipIntensityMaxTime;
    @SerializedName("precipProbability")
    @Expose
    public Float precipProbability;
    @SerializedName("precipType")
    @Expose
    public String precipType;
    @SerializedName("temperatureHigh")
    @Expose
    public Float temperatureHigh;
    @SerializedName("temperatureHighTime")
    @Expose
    public Integer temperatureHighTime;
    @SerializedName("temperatureLow")
    @Expose
    public Float temperatureLow;
    @SerializedName("temperatureLowTime")
    @Expose
    public Integer temperatureLowTime;
    @SerializedName("apparentTemperatureHigh")
    @Expose
    public Float apparentTemperatureHigh;
    @SerializedName("apparentTemperatureHighTime")
    @Expose
    public Integer apparentTemperatureHighTime;
    @SerializedName("apparentTemperatureLow")
    @Expose
    public Float apparentTemperatureLow;
    @SerializedName("apparentTemperatureLowTime")
    @Expose
    public Integer apparentTemperatureLowTime;
    @SerializedName("dewPoint")
    @Expose
    public Float dewPoint;
    @SerializedName("humidity")
    @Expose
    public Float humidity;
    @SerializedName("pressure")
    @Expose
    public Float pressure;
    @SerializedName("windSpeed")
    @Expose
    public Float windSpeed;
    @SerializedName("windGust")
    @Expose
    public Float windGust;
    @SerializedName("windGustTime")
    @Expose
    public Integer windGustTime;
    @SerializedName("windBearing")
    @Expose
    public Float windBearing;
    @SerializedName("cloudCover")
    @Expose
    public Float cloudCover;
    @SerializedName("uvIndex")
    @Expose
    public Float uvIndex;
    @SerializedName("uvIndexTime")
    @Expose
    public Integer uvIndexTime;
    @SerializedName("visibility")
    @Expose
    public Float visibility;
    @SerializedName("ozone")
    @Expose
    public Float ozone;
    @SerializedName("temperatureMin")
    @Expose
    public Float temperatureMin;
    @SerializedName("temperatureMinTime")
    @Expose
    public Integer temperatureMinTime;
    @SerializedName("temperatureMax")
    @Expose
    public Float temperatureMax;
    @SerializedName("temperatureMaxTime")
    @Expose
    public Integer temperatureMaxTime;
    @SerializedName("apparentTemperatureMin")
    @Expose
    public Float apparentTemperatureMin;
    @SerializedName("apparentTemperatureMinTime")
    @Expose
    public Integer apparentTemperatureMinTime;
    @SerializedName("apparentTemperatureMax")
    @Expose
    public Float apparentTemperatureMax;
    @SerializedName("apparentTemperatureMaxTime")
    @Expose
    public Integer apparentTemperatureMaxTime;

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(Integer sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public Integer getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(Integer sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public Float getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(Float moonPhase) {
        this.moonPhase = moonPhase;
    }

    public Float getPrecipIntensity() {
        return precipIntensity;
    }

    public void setPrecipIntensity(Float precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public Float getPrecipIntensityMax() {
        return precipIntensityMax;
    }

    public void setPrecipIntensityMax(Float precipIntensityMax) {
        this.precipIntensityMax = precipIntensityMax;
    }

    public Integer getPrecipIntensityMaxTime() {
        return precipIntensityMaxTime;
    }

    public void setPrecipIntensityMaxTime(Integer precipIntensityMaxTime) {
        this.precipIntensityMaxTime = precipIntensityMaxTime;
    }

    public Float getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(Float precipProbability) {
        this.precipProbability = precipProbability;
    }

    public String getPrecipType() {
        return precipType;
    }

    public void setPrecipType(String precipType) {
        this.precipType = precipType;
    }

    public Float getTemperatureHigh() {
        return temperatureHigh;
    }

    public void setTemperatureHigh(Float temperatureHigh) {
        this.temperatureHigh = temperatureHigh;
    }

    public Integer getTemperatureHighTime() {
        return temperatureHighTime;
    }

    public void setTemperatureHighTime(Integer temperatureHighTime) {
        this.temperatureHighTime = temperatureHighTime;
    }

    public Float getTemperatureLow() {
        return temperatureLow;
    }

    public void setTemperatureLow(Float temperatureLow) {
        this.temperatureLow = temperatureLow;
    }

    public Integer getTemperatureLowTime() {
        return temperatureLowTime;
    }

    public void setTemperatureLowTime(Integer temperatureLowTime) {
        this.temperatureLowTime = temperatureLowTime;
    }

    public Float getApparentTemperatureHigh() {
        return apparentTemperatureHigh;
    }

    public void setApparentTemperatureHigh(Float apparentTemperatureHigh) {
        this.apparentTemperatureHigh = apparentTemperatureHigh;
    }

    public Integer getApparentTemperatureHighTime() {
        return apparentTemperatureHighTime;
    }

    public void setApparentTemperatureHighTime(Integer apparentTemperatureHighTime) {
        this.apparentTemperatureHighTime = apparentTemperatureHighTime;
    }

    public Float getApparentTemperatureLow() {
        return apparentTemperatureLow;
    }

    public void setApparentTemperatureLow(Float apparentTemperatureLow) {
        this.apparentTemperatureLow = apparentTemperatureLow;
    }

    public Integer getApparentTemperatureLowTime() {
        return apparentTemperatureLowTime;
    }

    public void setApparentTemperatureLowTime(Integer apparentTemperatureLowTime) {
        this.apparentTemperatureLowTime = apparentTemperatureLowTime;
    }

    public Float getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(Float dewPoint) {
        this.dewPoint = dewPoint;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Float getPressure() {
        return pressure;
    }

    public void setPressure(Float pressure) {
        this.pressure = pressure;
    }

    public Float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Float getWindGust() {
        return windGust;
    }

    public void setWindGust(Float windGust) {
        this.windGust = windGust;
    }

    public Integer getWindGustTime() {
        return windGustTime;
    }

    public void setWindGustTime(Integer windGustTime) {
        this.windGustTime = windGustTime;
    }

    public Float getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(Float windBearing) {
        this.windBearing = windBearing;
    }

    public Float getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(Float cloudCover) {
        this.cloudCover = cloudCover;
    }

    public Float getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(Float uvIndex) {
        this.uvIndex = uvIndex;
    }

    public Integer getUvIndexTime() {
        return uvIndexTime;
    }

    public void setUvIndexTime(Integer uvIndexTime) {
        this.uvIndexTime = uvIndexTime;
    }

    public Float getVisibility() {
        return visibility;
    }

    public void setVisibility(Float visibility) {
        this.visibility = visibility;
    }

    public Float getOzone() {
        return ozone;
    }

    public void setOzone(Float ozone) {
        this.ozone = ozone;
    }

    public Float getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(Float temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public Integer getTemperatureMinTime() {
        return temperatureMinTime;
    }

    public void setTemperatureMinTime(Integer temperatureMinTime) {
        this.temperatureMinTime = temperatureMinTime;
    }

    public Float getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(Float temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public Integer getTemperatureMaxTime() {
        return temperatureMaxTime;
    }

    public void setTemperatureMaxTime(Integer temperatureMaxTime) {
        this.temperatureMaxTime = temperatureMaxTime;
    }

    public Float getApparentTemperatureMin() {
        return apparentTemperatureMin;
    }

    public void setApparentTemperatureMin(Float apparentTemperatureMin) {
        this.apparentTemperatureMin = apparentTemperatureMin;
    }

    public Integer getApparentTemperatureMinTime() {
        return apparentTemperatureMinTime;
    }

    public void setApparentTemperatureMinTime(Integer apparentTemperatureMinTime) {
        this.apparentTemperatureMinTime = apparentTemperatureMinTime;
    }

    public Float getApparentTemperatureMax() {
        return apparentTemperatureMax;
    }

    public void setApparentTemperatureMax(Float apparentTemperatureMax) {
        this.apparentTemperatureMax = apparentTemperatureMax;
    }

    public Integer getApparentTemperatureMaxTime() {
        return apparentTemperatureMaxTime;
    }

    public void setApparentTemperatureMaxTime(Integer apparentTemperatureMaxTime) {
        this.apparentTemperatureMaxTime = apparentTemperatureMaxTime;
    }
}
