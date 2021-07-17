package com.odiousPanda.rainbowKt.utilities

import android.content.Context
import com.odiousPanda.rainbowKt.R
import com.odiousPanda.rainbowKt.model.dataSource.weather.Weather
import java.lang.reflect.Field
import java.util.*

class ClothesIconUtil {
    private var criteria = "cool"
    private var rain = false
    private val head = mutableListOf<Int>()
    private val upper = mutableListOf<Int>()
    private val lower = mutableListOf<Int>()
    private val foot = mutableListOf<Int>()
    private val hand = mutableListOf<Int>()

    init {
        getIcons()
    }

    private fun getIcons() {
        val drawablesFields: Array<Field> = R.drawable::class.java.fields

        for (field in drawablesFields) {
            try {
                if (field.name.contains("head_")) {
                    if (field.name.contains(criteria)) {
                        head.add(field.getInt(null))
                    }
                } else if (field.name.contains("upper_")) {
                    if (field.name.contains(criteria)) {
                        upper.add(field.getInt(null))
                    }
                } else if (field.name.contains("lower_")) {
                    if (field.name.contains(criteria)) {
                        lower.add(field.getInt(null))
                    }
                } else if (field.name.contains("foot_")) {
                    if (field.name.contains(criteria)) {
                        foot.add(field.getInt(null))
                    }
                } else if (field.name.contains("hand_")) {
                    hand.add(field.getInt(null))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun generateCriteria(weather: Weather) {
        val temp =
            UnitConverter.toCelsius(weather.currently.apparentTemperature)
        rain = weather.currently.icon.contains("rain")
        criteria = when {
            temp > Constants.APT_TEMP_HOT -> {
                "hot"
            }
            temp < Constants.APT_TEMP_COLD -> {
                "cold"
            }
            else -> {
                "cool"
            }
        }
    }

    fun getCause(context: Context): String? {
        var criteriaString = context.getString(R.string.cool)
        if (criteria == "hot") {
            criteriaString = context.getString(R.string.hot)
        } else if (criteria == "cold") {
            criteriaString = context.getString(R.string.cold)
        }
        return context.resources.getString(R.string.cause_it_s) + if (rain) " $criteriaString ${context.getString(R.string.and_raining)}." else " $criteriaString."
    }

    fun getHeadIcon(): Int {
        return head[Random().nextInt(head.size)]
    }

    fun getUpperIcon(): Int {
        return upper[Random().nextInt(upper.size)]
    }

    fun getLowerIcon(): Int {
        return lower[Random().nextInt(lower.size)]
    }

    fun getFootIcon(): Int {
        return foot[Random().nextInt(foot.size)]
    }

    fun getHandIcon(): Int {
        return if (rain) {
            R.drawable.rain_ic_umbrella
        } else hand[Random().nextInt(hand.size)]
    }
}