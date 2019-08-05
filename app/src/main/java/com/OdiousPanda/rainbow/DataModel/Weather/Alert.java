package com.OdiousPanda.rainbow.DataModel.Weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Alert {
    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("expires")
    @Expose
    public long expires;

    @SerializedName("regions")
    @Expose
    public List<String> regions;


    @SerializedName("severity")
    @Expose
    /*
    The severity of the weather alert. Will take one of the following values:
     * "advisory" (an individual should be aware of potentially severe weather),
     * "watch" (an individual should prepare for potentially severe weather), or
     * "warning" (an individual should take immediate action to protect themselves and others from potentially severe weather).
     */
    public String severity;

    @SerializedName("time")
    @Expose
    public long time;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("uri")
    @Expose
    public String uri;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
