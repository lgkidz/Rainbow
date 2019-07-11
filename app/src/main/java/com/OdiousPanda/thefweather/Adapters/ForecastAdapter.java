package com.OdiousPanda.thefweather.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.OdiousPanda.thefweather.DataModel.Weather.Daily;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.PreferencesUtil;
import com.OdiousPanda.thefweather.Utilities.UnitConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private Context context;
    private Daily dailyData;
    private int textColor;

    public ForecastAdapter(Context context, Daily daily, int color) {
        this.context = context;
        this.dailyData = daily;
        this.textColor = color;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.forecast_item, parent, false);
        return new ForecastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(context);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date date = new Date((long) dailyData.getData().get(position).getTime() * 1000);
        String weekday = sdf.format(date);
        if (position == 0) {
            weekday = "Today";
        }
        String textTemp = UnitConverter.convertToTemperatureUnit(dailyData.getData().get(position).getTemperatureHigh(), currentTempUnit)
                + " - "
                + UnitConverter.convertToTemperatureUnit(dailyData.getData().get(position).getTemperatureLow(), currentTempUnit);
        String iconName = dailyData.getData().get(position).getIcon().replace("-", "_");
        if (textColor == Color.WHITE) {
            int iconResourceId = context.getResources().getIdentifier("drawable/" + iconName + "_w", null, context.getPackageName());
            holder.setData(weekday, textTemp, iconResourceId, textColor);
        } else {
            int iconResourceId = context.getResources().getIdentifier("drawable/" + iconName + "_b", null, context.getPackageName());
            holder.setData(weekday, textTemp, iconResourceId, textColor);
        }
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

        void setData(String date, String temp, int iconResource, int textColor) {
            this.date.setText(date);
            this.temp.setText(temp);
            this.icon.setImageResource(iconResource);
            this.date.setTextColor(textColor);
            this.temp.setTextColor(textColor);
        }
    }
}


