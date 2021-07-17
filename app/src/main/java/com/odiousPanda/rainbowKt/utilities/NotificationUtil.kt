package com.odiousPanda.rainbowKt.utilities

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.LocationServices
import com.odiousPanda.rainbowKt.activities.MainActivity
import com.odiousPanda.rainbowKt.apis.RetrofitService
import com.odiousPanda.rainbowKt.model.dataSource.weather.Weather
import kotlinx.coroutines.*
import java.util.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class NotificationUtil(private val context: Context) {
    companion object {
        const val NOTIFY_ACTION = "rainbow.notification.notify"
        private const val CHANNEL_NAME = "Rainbow Notification channel"
        private const val CHANNEL_DESCRIPTION = "Daily weather notification"
        private const val CHANNEL_ID = "rainbow.notification.channel.id"
        private const val NOTIFICATION_ID = 219808
        private const val ALARM_PENDING_REQUEST_CODE = 229808
    }

    private val alarmPendingIntent: PendingIntent

    init {
        createNotificationChannel()
        val intent = Intent(context, DailyNotificationAlarmReceiver::class.java)
        intent.action = NOTIFY_ACTION
        alarmPendingIntent =
            PendingIntent.getBroadcast(context, ALARM_PENDING_REQUEST_CODE, intent, 0)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun createNotification() {
        if (PermissionChecker.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            return
        }

        LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener {
            var locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0].language
            } else {
                context.resources.configuration.locale.language
            }
            if (locale != "vi") {
                locale = "en"
            }

            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitService.createWeatherCall()
                    .getWeather(it.latitude.toString(), it.longitude.toString(), locale)

                if (response.isSuccessful) {
                    response.body()?.let { weather -> createAndShowNotification(weather) }
                }
            }
        }
    }

    private fun createAndShowNotification(weather: Weather) {
        val today = weather.daily.data[0]
        val temp = (today.temperatureMin + today.temperatureMax) / 2
        val tempString = UnitConverter.convertToTemperatureUnit(
            temp,
            PreferencesUtil.getTemperatureUnit(context).toString()
        )
        val currentSummary = weather.currently.summary
        val summary = today.summary
        val iconName = today.icon.replace("-", "_")
        val iconRes = context.resources.getIdentifier(
            "drawable/" + iconName + "_b",
            null,
            context.packageName
        )
        val contentText = TextUtil.getNotificationText(
            context,
            temp,
            summary,
            today.precipType,
            today.precipProbability
        )
        val contentTitle = "$tempString - $currentSummary"
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(iconRes)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, iconRes))
            .setContentTitle(TextUtil.capitalizeSentence(contentTitle))
            .setContentText(TextUtil.capitalizeSentence(contentText))
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(TextUtil.capitalizeSentence(contentText))
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun startDailyNotification() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar =
            PreferencesUtil.getNotificationTime(context)//get notification time set by user
        calendar.add(Calendar.DATE, 1) //set the time to tomorrow
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmPendingIntent
        )
    }

    fun cancelDailyNotification() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmPendingIntent)
    }
}