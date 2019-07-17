package com.OdiousPanda.thefweather.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.OdiousPanda.thefweather.DataModel.Coordinate;
import com.OdiousPanda.thefweather.DataModel.LocationData;
import com.OdiousPanda.thefweather.R;
import com.OdiousPanda.thefweather.Repositories.WeatherRepository;
import com.OdiousPanda.thefweather.Utilities.PreferencesUtil;
import com.OdiousPanda.thefweather.Utilities.UnitConverter;

import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationItemViewHolder> {
    private Activity mActivity;
    private int activeItem = 0;
    private OnItemClickListener listener;
    private List<LocationData> mData;

    public LocationListAdapter(Activity context, List<LocationData> data, OnItemClickListener onItemClickListener) {
        this.mActivity = context;
        this.mData = data;
        this.listener = onItemClickListener;
    }

    @NonNull
    @Override
    public LocationItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mActivity).inflate(R.layout.location_list_item,parent,false);
        return new LocationItemViewHolder(v,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationItemViewHolder holder, int position) {
        LocationData model = mData.get(position);

        boolean firstItem = position == 0;

        String name = model.getCoordinate().getName();
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(mActivity);
        String temp = UnitConverter.convertToTemperatureUnit(model.getWeather().getCurrently().getTemperature(),currentTempUnit);
        String iconName = model.getWeather().getCurrently().getIcon().replace("-", "_");
        int iconResourceId = mActivity.getResources().getIdentifier("drawable/" + iconName + "_b", null, mActivity.getPackageName());
        holder.setData(firstItem,name,temp,iconResourceId);
        if(position == activeItem){
            holder.setCurrentItemIndicator(true);
        }
        else{
            holder.setCurrentItemIndicator(false);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Context getContext(){
        return mActivity;
    }

    public void updateLocationsData(List<LocationData> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        deleteItemFromDb(mData.get(position).getCoordinate());
        mData.remove(position);
        updateCurrentItem(position);
        notifyItemRemoved(position);
    }

    private void deleteItemFromDb(Coordinate coordinate){
        WeatherRepository.getInstance(mActivity).delete(coordinate);
    }

    private void updateCurrentItem(int position){
        if(position <= activeItem){
            activeItem--;
            listener.onItemClick(activeItem);
        }
    }

    class LocationItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView locationName;
        private TextView locationTemperature;
        private ImageView weatherIcon;
        private ImageView locationIcon;
        private View currentItemIndicator;
        private OnItemClickListener onItemClickListener;
        LocationItemViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            locationName = itemView.findViewById(R.id.location_name);
            locationTemperature = itemView.findViewById(R.id.location_temperature);
            weatherIcon = itemView.findViewById(R.id.location_weather_icon);
            locationIcon = itemView.findViewById(R.id.current_location_icon);
            currentItemIndicator = itemView.findViewById(R.id.current_item_indicator);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        void setData(boolean firstItem, String name, String temperature, int iconResource){
            locationIcon.setVisibility(firstItem ? View.VISIBLE : View.GONE);
            locationName.setText(name);
            locationTemperature.setText(temperature);
            weatherIcon.setImageResource(iconResource);
        }
        void setCurrentItemIndicator(boolean isCurrentItem){
            currentItemIndicator.setVisibility(isCurrentItem ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            notifyItemChanged(activeItem);
            activeItem = position;
            notifyItemChanged(activeItem);
            onItemClickListener.onItemClick(position);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

}
