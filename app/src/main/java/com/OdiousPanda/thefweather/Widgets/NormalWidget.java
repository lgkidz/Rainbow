package com.OdiousPanda.thefweather.Widgets;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.OdiousPanda.thefweather.API.RetrofitService;
import com.OdiousPanda.thefweather.API.WeatherCall;
import com.OdiousPanda.thefweather.Activities.MainActivity;
import com.OdiousPanda.thefweather.DataModel.Quote;
import com.OdiousPanda.thefweather.DataModel.Weather.Weather;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Service.WidgetTimeWorker;
import com.OdiousPanda.thefweather.Utilities.PreferencesUtil;
import com.OdiousPanda.thefweather.Utilities.UnitConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
    private static List<Quote> weatherQuotes = new ArrayList<>();
    private static List<Quote> quotes = new ArrayList<>();
    private static Quote quote;
    private static int widgetId;
    private static RemoteViews remoteViews;
    private static AppWidgetManager aWm;
    public static final String ACTION_UPDATE = "actionUpdate";
    public static final String ACTION_TAP = "widgetTap";
    public static final String ACTION_TO_DETAILS = "toDetailsScreen";
    private static final String TEMP_BITMAP = "tempBitmap";
    private static final String RF_BITMAP = "RFBitmap";
    private static final String MAIN_BITMAP = "mainBitmap";
    private static final String SUB_BITMAP = "subBitmap";
    public static final String TIME_BITMAP = "timeBitmap";
    public static final String DN_BITMAP = "dnBitmap";
    public static final String DATE_BITMAP = "dateBitmap";
    private static final String LOCATION_BITMAP = "locationBitmap";

    private static final int DOUBLE_CLICK_DELAY = 500;

    public static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.normal_widget);
        widgetId = appWidgetId;
        remoteViews = views;
        aWm = appWidgetManager;

        Date date = new Date();
        String timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM",Locale.getDefault());
        remoteViews.setImageViewBitmap(R.id.widget_time,textAsBitmap(context,timeString.substring(0,5),TIME_BITMAP));
        remoteViews.setImageViewBitmap(R.id.widget_day_night,textAsBitmap(context,timeString.substring(5), DN_BITMAP));
        remoteViews.setImageViewBitmap(R.id.widget_date,textAsBitmap(context,dateFormat.format(date),DATE_BITMAP));
        OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(WidgetTimeWorker.class).build();
        WorkManager.getInstance(context).enqueue(mRequest);

        if (PreferencesUtil.isNotFirstTimeLaunch(context)) {
            Intent tapIntent = new Intent(context,NormalWidget.class);
            tapIntent.setAction(ACTION_TAP);
            PendingIntent tapPending = PendingIntent.getBroadcast(context,0,tapIntent,0);
            remoteViews.setOnClickPendingIntent(R.id.widget_quote_layout,tapPending);
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            mainActivityIntent.setAction(ACTION_TO_DETAILS);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,widgetId,mainActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.layout_data,pendingIntent);
            PreferencesUtil.setWidgetTapCount(context,0);
            aWm.updateAppWidget(widgetId, remoteViews);
            updateData(context);
        }

    }

    public static Bitmap textAsBitmap(Context context,String text,String bitmapType){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        switch (bitmapType){
            case TEMP_BITMAP:{
                paint.setTextSize(context.getResources().getDimension(R.dimen.text_view_36sp));
                break;
            }
            case RF_BITMAP:{
                paint.setTextSize(context.getResources().getDimension(R.dimen.text_view_18sp));
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
            case TIME_BITMAP:{
                paint.setTextSize(context.getResources().getDimension(R.dimen.text_view_48sp));
                break;
            }
            case DN_BITMAP:{
                paint.setTextSize(context.getResources().getDimension(R.dimen.text_view_18sp));
                break;
            }
            case DATE_BITMAP:{
                paint.setTextSize(context.getResources().getDimension(R.dimen.text_view_14sp));
                break;
            }
            case LOCATION_BITMAP:{
                paint.setTextSize(context.getResources().getDimension(R.dimen.text_view_14sp));
                break;
            }
        }
        Typeface nunito = ResourcesCompat.getFont(context,R.font.nunito);
        paint.setTypeface(nunito);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        if (bitmapType.equals(MAIN_BITMAP)) {

            Bundle mAppWidgetOptions  = AppWidgetManager.getInstance(
                    context).getAppWidgetOptions(widgetId);
            int mWidgetPortHeight = mAppWidgetOptions
                    .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

            int mWidgetPortWidth = mAppWidgetOptions
                    .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(context.getResources().getDimension(R.dimen.text_view_54sp));
            if(mWidgetPortHeight * context.getResources().getDisplayMetrics().density + 0.5f < context.getResources().getDimension(R.dimen.widget_height) * 3){
                textPaint.setTextSize(context.getResources().getDimension(R.dimen.text_view_36sp));
            }
            textPaint.setColor(Color.WHITE);
            textPaint.setTypeface(nunito);
            int width = (int) (mWidgetPortWidth * context.getResources().getDisplayMetrics().density + 0.5f);
            if(width <= 0){
                width = (int) context.getResources().getDimension(R.dimen.widget_width);
            }

            StaticLayout staticLayout;
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                StaticLayout.Builder builder = StaticLayout.Builder.obtain(text,0,text.length(),textPaint,width)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(0.0f,1.0f)
                        .setIncludePad(false);
                staticLayout = builder.build();
            }
            else{
                staticLayout = new StaticLayout(text,textPaint,width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }

            int height = staticLayout.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            staticLayout.draw(canvas);
            return bitmap;
        }
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 1f); // round
        int height = (int) (baseline + paint.descent() + 1f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private static void updateData(final Context context){
        final String currentTempUnit = PreferencesUtil.getTemperatureUnit(context);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final WeatherCall call = RetrofitService.createWeatherCall();

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null){
                    return;
                }
                try {
                    Geocoder geo = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (!addresses.isEmpty()) {
                        String address = addresses.get(0).getAddressLine(0);
                        String[] addressPieces = address.split(",");
                        String locationName;
                        if(addressPieces.length >= 3){
                            locationName = addressPieces[addressPieces.length - 3].trim();
                        }
                        else{
                            locationName = addressPieces[addressPieces.length - 2].trim();
                        }
                        remoteViews.setImageViewBitmap(R.id.widget_location,textAsBitmap(context,locationName,LOCATION_BITMAP));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

                call.getWeather(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude())).enqueue(new Callback<Weather>() {
                    @Override
                    public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {
                        if(response.isSuccessful()){
                            weather = response.body();
                            assert weather != null;
                            String temp = UnitConverter.convertToTemperatureUnit(weather.getCurrently().getTemperature(),currentTempUnit);
                            String realFeelTemp = "Feels more like: " +  UnitConverter.convertToTemperatureUnit(weather.getCurrently().getApparentTemperature(),currentTempUnit);
                            remoteViews.setImageViewBitmap(R.id.tv_temp_widget,textAsBitmap(context,temp,TEMP_BITMAP));
                            remoteViews.setImageViewBitmap(R.id.tv_reaFeel_widget,textAsBitmap(context,realFeelTemp,RF_BITMAP));
                            String iconName = weather.getCurrently().getIcon().replace("-","_");
                            int iconResourceId = context.getResources().getIdentifier("drawable/" + iconName + "_w", null, context.getPackageName());
                            remoteViews.setImageViewResource(R.id.widget_icon,iconResourceId);
                            queryQuotes(context);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Weather> call, @NonNull Throwable t) {
                        remoteViews.setViewVisibility(R.id.widget_loading_layout,View.INVISIBLE);
                        aWm.updateAppWidget(widgetId, remoteViews);
                    }
                });
            }
        });

    }

    private static void queryQuotes(final Context context){
        FirebaseFirestore.getInstance().collection("quotes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Quote q = document.toObject(Quote.class);
                                quotes.add(q);
                            }
                        }
                        else {
                            remoteViews.setViewVisibility(R.id.widget_loading_layout,View.INVISIBLE);
                            aWm.updateAppWidget(widgetId, remoteViews);
                        }
                        getQuote(context,weather);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                remoteViews.setViewVisibility(R.id.widget_loading_layout,View.INVISIBLE);
                aWm.updateAppWidget(widgetId, remoteViews);
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
        if(temp > 27){
            criteria.add("hot");
        }
        else if(temp < 15){
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
        else if(summary.contains("snow") || summary.contains("sleet")){
            criteria.add("snow");
        }
        else if(summary.contains("hail")){
            criteria.add("hail");
        }
        else if(summary.contains("thunderstorm")){
            criteria.add("thunderstorm");
        }
        else if(summary.contains("tornado")){
            criteria.add("tornado");
        }
        else if(summary.contains("clear")){
            if(!criteria.contains("clear")){
                criteria.add("clear");
            }
        }
        weatherQuotes.clear();
        boolean isExplicit = PreferencesUtil.isExplicit(context);
        for(Quote q : quotes){
            for(String s: criteria){
                if(q.getAtt().contains("*")){
                    if(isExplicit){
                        q.setMain(censorStrongWords(q.getMain()));
                        q.setSub(censorStrongWords(q.getSub()));
                    }
                    weatherQuotes.add(q);
                    break;
                }
                if (q.getAtt().contains(s)){
                    if(isExplicit){
                        q.setMain(censorStrongWords(q.getMain()));
                        q.setSub(censorStrongWords(q.getSub()));
                    }
                    weatherQuotes.add(q);
                    break;
                }
            }
        }

        quote = weatherQuotes.get(new Random().nextInt(weatherQuotes.size()));
        if(quote.getMain() == null && quote.getSub() == null){
            quote.setDefaultQuote();
        }
        remoteViews.setImageViewBitmap(R.id.quote_main,textAsBitmap(context,quote.getMain(),MAIN_BITMAP));
        remoteViews.setImageViewBitmap(R.id.quote_sub,textAsBitmap(context,quote.getSub(),SUB_BITMAP));
        remoteViews.setViewVisibility(R.id.widget_loading_layout,View.INVISIBLE);
        aWm.updateAppWidget(widgetId, remoteViews);
    }

    private static String censorStrongWords(String text){
        String textNoStrongWords = text.toLowerCase().replace("fucking ","").trim();
        if(textNoStrongWords.length() > 0){
            return textNoStrongWords.substring(0, 1).toUpperCase() + textNoStrongWords.substring(1);
        }

        return text;
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
    public void onReceive(final Context context, Intent intent) {
        aWm = AppWidgetManager.getInstance(context);
        int[] ids = aWm.getAppWidgetIds(new ComponentName(context,NormalWidget.class));
        if(ids.length > 0){
            widgetId = ids[ids.length -1];
            Log.d("widgetTime", "onReceive: " + intent.getAction());
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.normal_widget);
            super.onReceive(context, intent);

            if(ACTION_UPDATE.equals(intent.getAction())){
                updateData(context);
            }

            if("com.sec.android.widgetapp.APPWIDGET_RESIZE".equals(intent.getAction())){
                if(quote != null){
                    remoteViews.setImageViewBitmap(R.id.quote_main,textAsBitmap(context,quote.getMain(),MAIN_BITMAP));
                    aWm.updateAppWidget(widgetId, remoteViews);
                }
            }

            if(ACTION_TAP.equals(intent.getAction())){
                int clickCount = PreferencesUtil.getWidgetTapCount(context);
                PreferencesUtil.setWidgetTapCount(context,++clickCount);

                @SuppressLint("HandlerLeak")
                final Handler handler = new Handler() {

                    public void handleMessage(Message msg){
                        int clickCount = PreferencesUtil.getWidgetTapCount(context);
                        if(clickCount > 1) {
                            remoteViews.setViewVisibility(R.id.widget_loading_layout,View.VISIBLE);
                            aWm.updateAppWidget(widgetId, remoteViews);
                            updateData(context);

                        }
                        PreferencesUtil.setWidgetTapCount(context,0);
                    }

                };

                if(clickCount == 1){
                    new Thread() {
                        @Override
                        public void run(){
                            try {
                                synchronized(this) { wait(DOUBLE_CLICK_DELAY); }
                                handler.sendEmptyMessage(0);
                            } catch(InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }
    }
}