package com.OdiousPanda.thefweather.DataModel.AQI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AirQuality {
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("data")
    @Expose
    public Data data;

    private String address;
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
