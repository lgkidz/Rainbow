package com.OdiousPanda.thefweather.Utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetWorkUtils {
    final static String BASE_URL_FORECAST = "http://api.openweathermap.org/data/2.5/forecast";
    final static String BASE_URL_CURRENT_WEATHER = "http://api.openweathermap.org/data/2.5/weather";

    public final static int TYPE_CURRENT_WEATHER = 0;
    public final static int TYPE_FORECAST = 1;

    final static String CITY_NAME_PARAM_QUERY = "q";

    final static String LATITUDE_PARAM_QUERRY = "lat";
    final static String LONGITUDE_PARAM_QUERRY = "lon";

    final static String API_KEY_PARAM = "APPID";
    final static String apiKey = "f405c50be75225cfb22cb07445daa564";

    public static URL buildURL(int type, String cityName, String lat, String lon){
        String baseUrl;
        switch (type){
            case 0:
                baseUrl = BASE_URL_CURRENT_WEATHER;
                break;
            case 1:
                baseUrl = BASE_URL_FORECAST;
                break;
            default:
                baseUrl = BASE_URL_CURRENT_WEATHER;
                break;
        }

        Uri builtUri;

        if(cityName != null){
             builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                     .appendQueryParameter(CITY_NAME_PARAM_QUERY,cityName)
                     .build();
        }
        else{
            builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .appendQueryParameter(LATITUDE_PARAM_QUERRY,lat)
                    .appendQueryParameter(LONGITUDE_PARAM_QUERRY,lon)
                    .build();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d("location", "buildURL: " + builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
