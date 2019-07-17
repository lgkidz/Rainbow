package com.OdiousPanda.thefweather.Service;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.OdiousPanda.thefweather.Widgets.NormalWidget;
import com.OdiousPanda.thefweather.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class WidgetTimeWorker extends Worker {
    private Context mContext;

    public WidgetTimeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        AppWidgetManager aWm = AppWidgetManager.getInstance(mContext);
        int[] ids = aWm.getAppWidgetIds(new ComponentName(mContext, NormalWidget.class));
        if (ids.length > 0) {
            int widgetId = ids[ids.length - 1];
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.normal_widget);
            Date date = new Date();
            String timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM");
            String dateString = dateFormat.format(date);
            remoteViews.setImageViewBitmap(R.id.widget_time, NormalWidget.textAsBitmap(mContext, timeString.substring(0, 5), NormalWidget.TIME_BITMAP));
            remoteViews.setImageViewBitmap(R.id.widget_day_night, NormalWidget.textAsBitmap(mContext, timeString.substring(5), NormalWidget.DN_BITMAP));
            remoteViews.setImageViewBitmap(R.id.widget_date, NormalWidget.textAsBitmap(mContext, dateString, NormalWidget.DATE_BITMAP));
            aWm.updateAppWidget(widgetId, remoteViews);
            OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(WidgetTimeWorker.class)
                    .setInitialDelay(3, TimeUnit.SECONDS)
                    .build();
            WorkManager.getInstance(mContext).enqueue(mRequest);

        }
        return Result.success();
    }
}
