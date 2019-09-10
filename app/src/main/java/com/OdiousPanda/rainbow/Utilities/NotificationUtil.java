package com.OdiousPanda.rainbow.Utilities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.PermissionChecker;

import com.OdiousPanda.rainbow.API.RetrofitService;
import com.OdiousPanda.rainbow.Activities.MainActivity;
import com.OdiousPanda.rainbow.DataModel.Weather.Datum_;
import com.OdiousPanda.rainbow.DataModel.Weather.Weather;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class NotificationUtil {
    static final String NOTIFY_ACTION = "rainbow.notification.notify";
    private final String channelName = "Rainbow Notification channel";
    private final String channelDescription = "Daily weather notification";
    private final String channelId = "rainbow.notification.channel.id";
    private final int notificationId = 219808;
    private final int alarmPendingIntentRequestCode = 229808;
    private Context context;
    private PendingIntent alarmPendingIntent;

    public NotificationUtil(Context context) {
        this.context = context;
        createNotificationChannel();

        Intent intent = new Intent(context, DailyNotificationAlarmReceiver.class);
        intent.setAction(NotificationUtil.NOTIFY_ACTION);
        alarmPendingIntent = PendingIntent.getBroadcast(context, alarmPendingIntentRequestCode, intent, 0);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    void createNotification() {
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                RetrofitService.createWeatherCall().getWeather(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())).enqueue(new Callback<Weather>() {
                    @Override
                    public void onResponse(@NonNull Call<Weather> call, @NonNull Response<Weather> response) {
                        if (response.isSuccessful()) {
                            Weather weather = response.body();
                            assert weather != null;
                            Datum_ today = weather.getDaily().getData().get(0);
                            float temp = (today.getTemperatureMax() + today.getTemperatureMin()) / 2;
                            String tempString = UnitConverter.convertToTemperatureUnit(temp, PreferencesUtil.getTemperatureUnit(context));
                            String currentSummary = weather.getCurrently().getSummary();
                            String summary = today.getSummary();
                            String iconName = today.getIcon().replace("-", "_");
                            int iconResourceId = context.getResources().getIdentifier("drawable/" + iconName + "_b", null, context.getPackageName());
                            String contentText = TextUtil.getNotificationText(context,temp, summary,today.getPrecipType(), today.getPrecipProbability());
                            String contentTitle = tempString + " - " + currentSummary;
                            Intent notificationIntent = new Intent(context, MainActivity.class);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                                    .setSmallIcon(iconResourceId)
                                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), iconResourceId))
                                    .setContentTitle(contentTitle)
                                    .setContentText(contentText)
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText(contentText))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setContentIntent(contentIntent)
                                    .setAutoCancel(true);
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(notificationId, builder.build());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Weather> call, @NonNull Throwable t) {

                    }
                });
            }
        });
    }

    public void startDailyNotification() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = PreferencesUtil.getNotificationTime(context); //get notification time set by user
        calendar.add(Calendar.DATE, 1);  //set the time to tomorrow
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmPendingIntent);
    }

    public void cancelDailyNotification() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmPendingIntent);
    }
}
