package com.OdiousPanda.thefweather.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.OdiousPanda.thefweather.DataModel.LocationData;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Utilities.PreferencesUtil;
import com.OdiousPanda.thefweather.Utilities.UnitConverter;

public class LocationListAdapter extends ListAdapter<LocationData,LocationListAdapter.LocationItemViewHolder> {
    private Context mContext;
    private int currentItem = 0;
    private OnItemClickListener listener;
    public LocationListAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.mContext = context;
    }

    private static final DiffUtil.ItemCallback<LocationData> DIFF_CALLBACK = new DiffUtil.ItemCallback<LocationData>() {
        @Override
        public boolean areItemsTheSame(@NonNull LocationData oldItem, @NonNull LocationData newItem) {
            return oldItem.getCoordinate().getId() == newItem.getCoordinate().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull LocationData oldItem, @NonNull LocationData newItem) {
            return oldItem.getCoordinate().getId() == newItem.getCoordinate().getId();
        }
    };

    @NonNull
    @Override
    public LocationItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.location_list_item,parent,false);
        return new LocationItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationItemViewHolder holder, int position) {
        LocationData model = getItem(position);
        boolean firstItem = true;
        if(position > 0){
            firstItem = false;
        }
        String name = model.getCoordinate().getName();
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(mContext);
        String temp = UnitConverter.convertToTemperatureUnit(model.getWeather().getCurrently().getTemperature(),currentTempUnit);
        String iconName = model.getWeather().getCurrently().getIcon().replace("-", "_");
        int iconResourceId = mContext.getResources().getIdentifier("drawable/" + iconName + "_b", null, mContext.getPackageName());
        holder.setData(firstItem,name,temp,iconResourceId);
        if(position == currentItem){
            holder.setCurrentItemIndicator(true);
        }
        else{
            holder.setCurrentItemIndicator(false);
        }
    }

    public void setCurrentItemPosition(int position){
        currentItem = position;
        notifyDataSetChanged();
        listener.onItemClick(position);
    }

    class LocationItemViewHolder extends RecyclerView.ViewHolder{
        private TextView locationName;
        private TextView locationTemperature;
        private ImageView weatherIcon;
        private ImageView locationIcon;
        private View currentItemIndicator;
        LocationItemViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.location_name);
            locationTemperature = itemView.findViewById(R.id.location_temperature);
            weatherIcon = itemView.findViewById(R.id.location_weather_icon);
            locationIcon = itemView.findViewById(R.id.current_location_icon);
            currentItemIndicator = itemView.findViewById(R.id.current_item_indicator);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    setCurrentItemPosition(position);
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
        }

        void setData(boolean firstItem, String name, String temperature, int iconResource){
            if(!firstItem){
                locationIcon.setVisibility(View.GONE);
            }
            locationName.setText(name);
            locationTemperature.setText(temperature);
            weatherIcon.setImageResource(iconResource);
        }
        void setCurrentItemIndicator(boolean isCurrentItem){
            if(isCurrentItem){
                currentItemIndicator.setVisibility(View.VISIBLE);
            }
            else{
                currentItemIndicator.setVisibility(View.INVISIBLE);
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
