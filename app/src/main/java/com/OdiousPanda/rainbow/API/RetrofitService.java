package com.OdiousPanda.rainbow.API;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

    private static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    private static Retrofit retrofitNearbySearch = new Retrofit.Builder()
            .baseUrl(Constant.GOOGLE_MAP_API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static Retrofit retrofitWeather = new Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static Retrofit retrofitAQI = new Retrofit.Builder()
            .baseUrl(Constant.AQI_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static Retrofit retrofitUnSplash = new Retrofit.Builder()
            .baseUrl(Constant.UNPLASH_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    static {
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public static NearbySearchCall createNearbySearchCall() {
        return retrofitNearbySearch.create(NearbySearchCall.class);
    }

    public static WeatherCall createWeatherCall() {
        return retrofitWeather.create(WeatherCall.class);
    }

    public static AQICall createAQICall() {
        return retrofitAQI.create(AQICall.class);
    }

    public static UnsplashCall createUnsplashCall() {
        return retrofitUnSplash.create(UnsplashCall.class);
    }
}
