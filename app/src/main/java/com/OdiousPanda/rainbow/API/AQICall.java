package com.OdiousPanda.rainbow.API;

import com.OdiousPanda.rainbow.DataModel.AQI.AirQuality;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AQICall {
    @GET("feed/geo:{lat};{lon}/?token=" + Constant.AQI_API_KEY)
    Call<AirQuality> getAirQuality(@Path("lat") String lat, @Path("lon") String lon);
}
