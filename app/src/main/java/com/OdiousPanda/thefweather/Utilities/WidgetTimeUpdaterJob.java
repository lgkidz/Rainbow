package com.OdiousPanda.thefweather.Utilities;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.OdiousPanda.thefweather.Service.WidgetTimeUpdater;

public class WidgetTimeUpdaterJob {
    @TargetApi(Build.VERSION_CODES.M)
    public static void scheduleJob(Context context){
        ComponentName serviceComponent = new ComponentName(context, WidgetTimeUpdater.class);
        JobInfo.Builder builder = new JobInfo.Builder(0,serviceComponent);
        builder.setMinimumLatency(3000);
        builder.setOverrideDeadline(5000);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());

    }
}
