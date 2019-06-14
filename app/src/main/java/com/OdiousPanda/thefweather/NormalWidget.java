package com.OdiousPanda.thefweather;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.OdiousPanda.thefweather.API.RetrofitService;
import com.OdiousPanda.thefweather.API.WeatherCall;
import com.OdiousPanda.thefweather.Activities.MainActivity;
import com.OdiousPanda.thefweather.Model.Quote;
import com.OdiousPanda.thefweather.Model.Weather.Weather;
import com.OdiousPanda.thefweather.Utilities.UnitConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

/**
 * Implementation of App Widget functionality.
 */
public class NormalWidget extends AppWidgetProvider {

    private static Weather weather;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static List<Quote> weatherQuotes = new ArrayList<>();
    private static List<Quote> quotes = new ArrayList<>();
    private static Quote quote;
    private static int widgetId;
    private static RemoteViews remoteViews;
    private static AppWidgetManager aWm;
    public static final String ACTION_UPDATE = "actionUpdate";
    private static final String TEMP_BITMAP = "tempBitmap";
    private static final String RF_BITMAP = "RFBitmap";
    private static final String MAIN_BITMAP = "mainBitmap";
    private static final String SUB_BITMAP = "subBitmap";

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_key_string), Context.MODE_PRIVATE);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.normal_widget);
        widgetId = appWidgetId;
        remoteViews = views;
        aWm = appWidgetManager;
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,widgetId,mainActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.layout_data,pendingIntent);

        long lastUpdate = Long.parseLong(sharedPreferences.getString(context.getString(R.string.widget_update_time_pref), "0"));
        long currentTime = System.currentTimeMillis();
        //auto update every 30 minutes
        if (currentTime - lastUpdate > 1800000) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            updateData(context,sharedPreferences);
            editor.putString(context.getString(R.string.widget_update_time_pref),String.valueOf(currentTime));
        }
    }

    private static Bitmap textAsBitmap(Context context,String text,String bitmapType){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        switch (bitmapType){
            case TEMP_BITMAP:{
                paint.setTextSize(context.getResources().getDimension(R.dimen.text_view_48sp));
                break;
            }
            case RF_BITMAP:{
                paint.setTextSize(context.getResources().getDimension(R.dimen.text_view_20sp));
                break;
            }
            case MAIN_BITMAP:{
                paint.setTextSize(context.getResources().getDimension(R.dimen.text_view_54sp));
                break;
            }
            case SUB_BITMAP:{
                paint.setTextSize(context.getResources().getDimension(R.dimen.text_view_24sp));
                break;
            }
        }
        Typeface nunito = ResourcesCompat.getFont(context,R.font.nunito);
        paint.setTypeface(nunito);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        if (bitmapType.equals(MAIN_BITMAP)) {
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(context.getResources().getDimension(R.dimen.text_view_54sp));
            textPaint.setColor(Color.WHITE);
            textPaint.setTypeface(nunito);
            int width = (int) (context.getResources().getDimension(R.dimen.widget_width) *5/4);
            StaticLayout staticLayout = new StaticLayout(text,textPaint,width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            int height = staticLayout.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            staticLayout.draw(canvas);
            return bitmap;
        }
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private static void updateData(final Context context, SharedPreferences sharedPreferences){
        final String currentTempUnit = sharedPreferences.getString(context.getString(R.string.pref_temp), context.getString(R.string.temp_setting_degree_c));
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final WeatherCall call = RetrofitService.createWeatherCall();

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                call.getWeather(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude())).enqueue(new Callback<Weather>() {
                    @Override
                    public void onResponse(Call<Weather> call, Response<Weather> response) {
                        if(response.isSuccessful()){
                            weather = response.body();
                            String temp = UnitConverter.convertToTemperatureUnit(weather.getCurrently().getTemperature(),currentTempUnit);
                            String realFeelTemp = "Feels like: " +  UnitConverter.convertToTemperatureUnit(weather.getCurrently().getApparentTemperature(),currentTempUnit);
                            remoteViews.setImageViewBitmap(R.id.tv_temp_widget,textAsBitmap(context,temp,TEMP_BITMAP));
                            remoteViews.setImageViewBitmap(R.id.tv_reaFeel_widget,textAsBitmap(context,realFeelTemp,RF_BITMAP));
                            String iconName = weather.getCurrently().getIcon().replace("-","_");
                            int iconResourceId = context.getResources().getIdentifier("drawable/" + iconName + "_w", null, context.getPackageName());
                            remoteViews.setImageViewResource(R.id.widget_icon,iconResourceId);
                            queryQuotes(context);
                        }
                    }

                    @Override
                    public void onFailure(Call<Weather> call, Throwable t) {

                    }
                });
            }
        });

    }

    private static void queryQuotes(final Context context){
        db.collection("quotes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Quote q = document.toObject(Quote.class);
                                quotes.add(q);
                            }
                        }
                        else {

                        }
                        getQuote(context,weather);
                    }
                });
    }

    private static void getQuote(Context context,Weather w){
        weather = w;
        if(quotes.size() == 0){
            queryQuotes(context);
            return;
        }

        float temp = UnitConverter.toCelsius(weather.getCurrently().getApparentTemperature());
        String summary = weather.getCurrently().getIcon();
        List<String> criteria = new ArrayList<>();
        if(temp > 30){
            criteria.add("hot");
        }
        else if(temp < 10){
            criteria.add("cold");
        }
        else{
            criteria.add("clear");
        }

        if(summary.contains("rain")){
            criteria.add("rain");
        }
        else if(summary.contains("cloudy")){
            criteria.add("cloudy");
        }
        else if(summary.contains("fog")){
            criteria.add("fog");
        }
        else if(summary.contains("clear")){
            if(!criteria.contains("clear")){
                criteria.add("clear");
            }
        }

        for(Quote q : quotes){
            for(String s: criteria){
                if (q.getAtt().contains(s)){
                    weatherQuotes.add(q);
                    break;
                }
            }
        }

        quote = weatherQuotes.get(new Random().nextInt(weatherQuotes.size()));
        remoteViews.setImageViewBitmap(R.id.quote_main,textAsBitmap(context,quote.getMain(),MAIN_BITMAP));
        remoteViews.setImageViewBitmap(R.id.quote_sub,textAsBitmap(context,quote.getSub(),SUB_BITMAP));
        aWm.updateAppWidget(widgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(final Context context) {

    }

    @Override
    public void onDisabled(Context context) {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_key_string), Context.MODE_PRIVATE);
        String currentTempUnit = sharedPreferences.getString(context.getString(R.string.pref_temp), context.getString(R.string.temp_setting_degree_c));
        super.onReceive(context, intent);
        if(ACTION_UPDATE.equals(intent.getAction())){
            String temp = UnitConverter.convertToTemperatureUnit(weather.getCurrently().getTemperature(),currentTempUnit);
            String realFeelTemp = "Feels like: " +  UnitConverter.convertToTemperatureUnit(weather.getCurrently().getApparentTemperature(),currentTempUnit);
            //remoteViews.setTextViewText(R.id.tv_temp_widget,temp);
            remoteViews.setImageViewBitmap(R.id.tv_temp_widget,textAsBitmap(context,temp,TEMP_BITMAP));
            remoteViews.setImageViewBitmap(R.id.tv_reaFeel_widget,textAsBitmap(context,realFeelTemp,RF_BITMAP));
            aWm.updateAppWidget(widgetId, remoteViews);
        }
    }
}

