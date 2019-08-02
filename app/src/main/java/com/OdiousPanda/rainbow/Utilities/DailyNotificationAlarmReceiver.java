package com.OdiousPanda.rainbow.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DailyNotificationAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtil notificationUtil = new NotificationUtil(context);
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            notificationUtil.startDailyNotification();
        } else if (intent.getAction().equals(NotificationUtil.NOTIFY_ACTION)) {
            notificationUtil.createNotification();
        }
    }
}
