package com.OdiousPanda.rainbow.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.OdiousPanda.rainbow.DataModel.Weather.Daily;
import com.OdiousPanda.rainbow.R;
import com.OdiousPanda.rainbow.Utilities.PreferencesUtil;
import com.OdiousPanda.rainbow.Utilities.UnitConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.ForecastViewHolder> {
    private Context context;
    private Daily dailyData;

    public DailyForecastAdapter(Context context, Daily daily) {
        this.context = context;
        this.dailyData = daily;
    }


    @NonNull
    @Override
    public DailyForecastAdapter.ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.daily_forecast_item, parent, false);
        return new DailyForecastAdapter.ForecastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyForecastAdapter.ForecastViewHolder holder, int position) {
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(context);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date date = new Date((long) dailyData.getData().get(position).getTime() * 1000);
        String weekday = sdf.format(date);
        float temp = (dailyData.getData().get(position).getTemperatureMax() + dailyData.getData().get(position).getTemperatureMin()) / 2;
        String textTemp = UnitConverter.convertToTemperatureUnitClean(temp, currentTempUnit);
        String iconName = dailyData.getData().get(position).getIcon().replace("-", "_");
        int iconResourceId = context.getResources().getIdentifier("drawable/" + iconName + "_b", null, context.getPackageName());
        holder.setData(weekday, textTemp, iconResourceId);
    }

    @Override
    public int getItemCount() {
        return dailyData.getData().size() - 1;
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private ImageView icon;
        private TextView temp;

        ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_forecast);
            icon = itemView.findViewById(R.id.icon_forecast);
            temp = itemView.findViewById(R.id.temp_forecast);
        }

        void setData(String date, String temp, int iconResource) {
            this.date.setText(date);
            this.temp.setText(temp);
            this.icon.setImageResource(iconResource);
        }
    }
}
