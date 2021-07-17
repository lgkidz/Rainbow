package com.odiousPanda.rainbowKt.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.odiousPanda.rainbowKt.databinding.LocationListItemBinding
import com.odiousPanda.rainbowKt.model.dataSource.LocationData
import com.odiousPanda.rainbowKt.utilities.PreferencesUtil
import com.odiousPanda.rainbowKt.utilities.UnitConverter

class LocationListAdapter(
    private val context: Context,
    private val listener: OnLocationListAdapterActionListener
) : RecyclerView.Adapter<LocationListAdapter.LocationItemViewHolder>() {
    private val data = mutableListOf<LocationData>()
    private var currentLocationData = LocationData()
    private var activeItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationItemViewHolder {
        val binding =
            LocationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationItemViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: LocationItemViewHolder, position: Int) {
        val model = data[position]
        val firstItem = position == 0
        val name: String = model.coordinate.name
        val currentTempUnit = PreferencesUtil.getTemperatureUnit(context)
        val temp = model.weather?.currently?.temperature?.let {
            UnitConverter.convertToTemperatureUnit(it, currentTempUnit!!)
        }
        val iconName = model.weather?.currently?.icon?.replace("-", "_")
        val iconResourceId = context.resources.getIdentifier(
            "drawable/" + iconName + "_b",
            null,
            context.packageName
        )
        holder.setData(firstItem, name, temp.toString(), iconResourceId)
        if (position == activeItem) {
            holder.setCurrentItemIndicator(true)
        } else {
            holder.setCurrentItemIndicator(false)
        }
        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            notifyItemChanged(position)
            activeItem = pos
            notifyDataSetChanged()
            listener.onItemClick(position)
        }
    }

    fun updateCurrentLocationData(locationData: LocationData) {
        currentLocationData = locationData
        if(data.isNotEmpty()) {
            data[0] = currentLocationData
        } else {
            data.add(0, currentLocationData)
        }
        notifyItemChanged(0)
    }

    fun updateUserAddedLocationData(dataList: List<LocationData>) {
        data.clear()
        data.add(0, currentLocationData)
        data.addAll(1, dataList)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
        listener.onDeleteItem(position)
        if (position == activeItem) {
            activeItem--
            notifyItemChanged(activeItem)
        }
    }

    fun updateDataAt(position: Int, locationData: LocationData) {
        data[position] = locationData
        notifyItemChanged(position)
    }

    fun getContext(): Context = context

    class LocationItemViewHolder(private val binding: LocationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(isFirstItem: Boolean, name: String, temperature: String, iconResource: Int) {
            binding.currentLocationIcon.visibility =
                if (isFirstItem) View.VISIBLE else View.INVISIBLE
            binding.locationName.text = name
            binding.locationTemperature.text = temperature
            binding.locationWeatherIcon.setImageResource(iconResource)
        }

        fun setCurrentItemIndicator(isCurrentItem: Boolean) {
            binding.currentItemIndicator.visibility =
                if (isCurrentItem) View.VISIBLE else View.INVISIBLE
        }
    }

    interface OnLocationListAdapterActionListener {
        fun onItemClick(position: Int)
        fun onDeleteItem(position: Int)
    }
}

