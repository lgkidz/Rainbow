package com.odiousPanda.rainbowKt.apis

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private val interceptor = HttpLoggingInterceptor()

    init {
        interceptor.level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

    private val retrofitWeather = Retrofit.Builder()
        .baseUrl(APIConstants.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun createWeatherCall(): WeatherCall {
        return retrofitWeather.create(WeatherCall::class.java)
    }

    private val retrofitNearbySearch = Retrofit.Builder()
        .baseUrl(APIConstants.GOOGLE_MAP_API_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitAQI = Retrofit.Builder()
        .baseUrl(APIConstants.AQI_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitUnSplash = Retrofit.Builder()
        .baseUrl(APIConstants.UNPLASH_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun createAQICall(): AQICall {
        return retrofitAQI.create(AQICall::class.java)
    }

    fun createUnsplashCall(): UnsplashCall {
        return retrofitUnSplash.create(UnsplashCall::class.java)
    }

    fun createNearbySearchCall(): NearbySearchCall {
        return retrofitNearbySearch.create(NearbySearchCall::class.java)
    }

}