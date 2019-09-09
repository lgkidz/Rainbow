package com.OdiousPanda.rainbow.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.OdiousPanda.rainbow.DataModel.Weather.Datum;
import com.OdiousPanda.rainbow.R;
import com.OdiousPanda.rainbow.Utilities.PreferencesUtil;
import com.OdiousPanda.rainbow.Utilities.UnitConverter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastItem> {

    private List<Datum> mData;
    private Context mContext;

    public HourlyForecastAdapter(List<Datum> data, Context context) {
        this.mData = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public HourlyForecastItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.hourly_forecast_item, parent, false);
        return new HourlyForecastItem(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastItem holder, int position) {
        holder.setIsRecyclable(false);
        String temp = UnitConverter.convertToTemperatureUnitClean(mData.get(position).getTemperature(), PreferencesUtil.getTemperatureUnit(mContext));
        String iconName = mData.get(position).getIcon().replace("-", "_");
        int iconResourceId = mContext.getResources().getIdentifier("drawable/" + iconName + "_b", null, mContext.getPackageName());
        SimpleDateFormat df = new SimpleDateFormat("h a", Locale.getDefault());
        String time = position == 0 ? mContext.getString(R.string.now) : df.format(mData.get(position).getTime() * 1000).toLowerCase();
        String precip = (int) (mData.get(position).getPrecipProbability() * 100) + "%";
        if ((int) (mData.get(position).getPrecipProbability() * 100) == 0) {
            holder.precipitation.setVisibility(View.INVISIBLE);

        }
        if (position == 0) {
            holder.itemLayout.setBackgroundResource(R.drawable.hourly_first_item_background);
        }
        holder.setData(temp, iconResourceId, precip, time);
    }

    @Override
    public int getItemCount() {
        return mData.size() / 2;
    }

    class HourlyForecastItem extends RecyclerView.ViewHolder {
        ConstraintLayout itemLayout;
        TextView precipitation;
        private TextView temperature;
        private ImageView icon;
        private TextView hour;

        HourlyForecastItem(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.hourlyItemLayout);
            temperature = itemView.findViewById(R.id.hourlyTemp);
            icon = itemView.findViewById(R.id.hourlyIcon);
            precipitation = itemView.findViewById(R.id.hourlyPrecipitation);
            hour = itemView.findViewById(R.id.hourlyHour);
        }

        void setData(String temp, int iconResource, String precip, String time) {
            temperature.setText(temp);
            icon.setImageResource(iconResource);
            precipitation.setText(precip);
            hour.setText(time);
        }
    }
}
