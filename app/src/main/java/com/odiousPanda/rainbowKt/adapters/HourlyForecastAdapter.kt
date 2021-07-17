package com.odiousPanda.rainbowKt.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.odiousPanda.rainbowKt.R
import com.odiousPanda.rainbowKt.databinding.HourlyForecastItemBinding
import com.odiousPanda.rainbowKt.model.dataSource.weather.DataX
import com.odiousPanda.rainbowKt.utilities.PreferencesUtil
import com.odiousPanda.rainbowKt.utilities.UnitConverter
import java.text.SimpleDateFormat
import java.util.*

class HourlyForecastAdapter(private val context: Context, private val data: List<DataX>) :
    RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastItem>() {

    class HourlyForecastItem(private val binding: HourlyForecastItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(temp: String, iconRes: Int, precipitation: Int, time: String) {
            binding.hourlyTemp.text = temp
            binding.hourlyIcon.setImageResource(iconRes)
            val precipitationString = "$precipitation%"
            binding.hourlyPrecipitation.text = precipitationString
            binding.hourlyHour.text = time

            if (precipitation == 0) {
                binding.hourlyPrecipitation.visibility = View.INVISIBLE
            }

            if (adapterPosition == 0) {
                binding.hourlyItemLayout.setBackgroundResource(R.drawable.hourly_first_item_background)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastItem {
        val binding =
            HourlyForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyForecastItem(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HourlyForecastItem, position: Int) {
        holder.setIsRecyclable(false)
        val temp = UnitConverter.convertToTemperatureUnitClean(
            data[position].temperature,
            PreferencesUtil.getTemperatureUnit(context).toString()
        ).toString()
        val iconName = data[position].icon.replace("-", "_")
        val iconRes = context.resources.getIdentifier(
            "drawable/" + iconName + "_b",
            null,
            context.packageName
        )
        val df = SimpleDateFormat("h a", Locale.getDefault())
        val time =
            if (position == 0) context.getString(R.string.now) else df.format(data[position].time * 1000)
                .toLowerCase(Locale.ROOT)
        val precipitation = (data[position].precipProbability * 100).toInt()
        holder.setData(temp, iconRes, precipitation, time)
    }
}