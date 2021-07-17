package com.odiousPanda.rainbowKt.utilities

import android.content.Context
import com.odiousPanda.rainbowKt.R
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.roundToInt

object UnitConverter {
    private const val DEGREE = "\u00b0"

    private fun banana(): String {
        val bananaByte = byteArrayOf(
            0xF0.toByte(),
            0x9F.toByte(),
            0x8D.toByte(),
            0x8C.toByte()
        )
        return String(bananaByte, StandardCharsets.UTF_8)
    }

    fun toCelsius(temp: Float): Float {
        return (temp - 32) * 5 / 9
    }

    private fun toFahrenheit(temp: Float): Float {
        return temp
    }

    private fun toCelsiusPretty(temp: Float): String {
        return "${toCelsius(temp).roundToInt()}${DEGREE}C"
    }

    private fun toFahrenheitPretty(temp: Float): String {
        return "${toFahrenheit(temp).roundToInt()}${DEGREE}F"
    }

    private fun toKelvin(temp: Float): Float {
        return toCelsius(temp) + 273
    }

    private fun toKelvinPretty(temp: Float): String {
        return "${toKelvin(temp).roundToInt()}$DEGREE}K"
    }

    fun convertToTemperatureUnit(temp: Float, unit: String): String {
        if (unit == "${DEGREE}C") {
            return toCelsiusPretty(temp)
        } else if (unit == "${DEGREE}F") {
            return toFahrenheitPretty(temp)
        }
        return toKelvinPretty(temp)
    }

    fun convertToTemperatureUnitClean(
        temp: Float,
        unit: String
    ): String? {
        if (unit == "${DEGREE}C") {
            return toCelsius(temp).roundToInt().toString() + DEGREE
        } else if (unit == "${DEGREE}F") {
            return toFahrenheit(temp).roundToInt().toString() + DEGREE
        }
        return "${toKelvin(temp).roundToInt()}${DEGREE}K"
    }

    fun convertToDistanceUnit(distance: Float, unit: String): String {
        if (unit == "km") {
            return "${(distance * 1.609).roundToInt()} km"
        } else if (unit == "mile") {
            return "${distance.roundToInt()} mi"
        }
        return "${(distance * 9041.254).roundToInt()} ${banana()}"
    }

    fun convertToSpeedUnit(speed: Float, unit: String): String {
        if (unit == "kmph") {
            return "${(speed * 1.609).roundToInt()} km/h"
        } else if (unit == "mph") {
            return "${speed.roundToInt()} mph"
        }
        return "${(speed * 9041.254).roundToInt()} ${banana()}/h"
    }

    fun toMeterPerSecond(speed: Float): Float {
        return (speed * 0.447).toFloat()
    }

    fun convertToPressureUnit(
        context: Context,
        pressure: Float,
        unit: String
    ): String? {
        return when (unit) {
            "psi" -> {
                "${(pressure / 68.948).roundToInt()} psi"
            }
            "mmHg" -> {
                "${(pressure / 1.333).roundToInt()} mmHg"
            }
            else -> {
                val depressLevels =
                    context.resources.getStringArray(R.array.depression_levels)
                depressLevels[Random().nextInt(depressLevels.size)]
            }
        }
    }
}