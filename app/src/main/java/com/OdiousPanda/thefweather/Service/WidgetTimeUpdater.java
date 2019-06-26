package com.OdiousPanda.thefweather.Service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;


import com.OdiousPanda.thefweather.NormalWidget;
import com.OdiousPanda.thefweather.Utilities.WidgetTimeUpdaterJob;

public class WidgetTimeUpdater extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        sendBroadcast(new Intent(NormalWidget.ACTION_UPDATE_TIME));
        WidgetTimeUpdaterJob.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
