package com.odiousPanda.rainbowKt.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class DailyNotificationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationUtil = NotificationUtil(context)
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            notificationUtil.startDailyNotification()
        } else if (intent.action == NotificationUtil.NOTIFY_ACTION) {
            notificationUtil.createNotification()
        }
    }
}