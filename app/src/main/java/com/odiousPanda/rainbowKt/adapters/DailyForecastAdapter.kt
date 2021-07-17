package com.odiousPanda.rainbowKt.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.odiousPanda.rainbowKt.databinding.DailyForecastItemBinding
import com.odiousPanda.rainbowKt.model.dataSource.weather.Daily
import com.odiousPanda.rainbowKt.utilities.PreferencesUtil
import com.odiousPanda.rainbowKt.utilities.UnitConverter
import java.text.SimpleDateFormat
import java.util.*

class DailyForecastAdapter(private val context: Context, private val daily: Daily) :
    RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val binding =
            DailyForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyForecastViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return daily.data.size
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val currentTempUnit = PreferencesUtil.getTemperatureUnit(context).toString()
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        val weekDay = sdf.format(daily.data[position].time * 1000)
        val temp = (daily.data[position].temperatureMax + daily.data[position].temperatureMin) / 2
        val tempString =
            UnitConverter.convertToTemperatureUnitClean(temp, currentTempUnit).toString()
        val iconName = daily.data[position].icon.replace("-", "_")
        val iconRes =
            context.resources.getIdentifier("drawable/${iconName}_b", null, context.packageName)
        holder.setData(weekDay, tempString, iconRes)
    }

    class DailyForecastViewHolder(private val binding: DailyForecastItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(date: String, temp: String, iconRes: Int) {
            binding.dateForecast.text = date
            binding.tempForecast.text = temp
            binding.iconForecast.setImageResource(iconRes)
        }
    }
}