package com.OdiousPanda.thefweather.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.OdiousPanda.thefweather.NormalWidget;

import java.util.Timer;
import java.util.TimerTask;

public class WidgetTimeUpdater extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendBroadcast(new Intent(NormalWidget.ACTION_UPDATE_TIME));
            }
        },0,1000*5);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, WidgetTimeUpdater.class));
    }
}
