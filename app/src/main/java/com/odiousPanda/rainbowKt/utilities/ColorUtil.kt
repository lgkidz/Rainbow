package com.odiousPanda.rainbowKt.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import com.odiousPanda.rainbowKt.R
import java.util.*

object ColorUtil {
    fun randomColorCode(): IntArray {
        return intArrayOf(
            255,
            Random().nextInt(256),
            Random().nextInt(256),
            Random().nextInt(256)
        )
    }

    fun blackOrWhiteOf(argb: IntArray): Int {
        val r = argb[1]
        val g = argb[2]
        val b = argb[3]
        return if (r * 0.299 + g * 0.587 + b * 0.114 > 186) {
            Color.argb(255, 0, 0, 0)
        } else Color.argb(255, 255, 255, 255)
    }

    fun blackOrWhiteOf(color: Int): Int {
        val hexColor = String.format("#%06X", 0xFFFFFF and color)
        val rgb = intArrayOf(
            hexColor.substring(1, 3).toInt(16),
            hexColor.substring(3, 5).toInt(16),
            hexColor.substring(5, 7).toInt(16)
        )
        val r = rgb[0]
        val g = rgb[1]
        val b = rgb[2]
        return if (r * 0.299 + g * 0.587 + b * 0.114 > 186) {
            Color.argb(255, 0, 0, 0)
        } else Color.argb(255, 255, 255, 255)
    }

    fun invertColor(argb: IntArray): Int {
        val r = 255 - argb[1]
        val g = 255 - argb[2]
        val b = 255 - argb[3]
        return Color.argb(255, r, g, b)
    }

    @SuppressLint("ResourceType")
    fun getTemperaturePointerColor(
        context: Context,
        offset: Float
    ): Int {
        val coldBlueString = context.resources.getString(R.color.coldBlue)
        val hotPinkString = context.resources.getString(R.color.hotPink)
        val coldBlue = intArrayOf(
            coldBlueString.substring(3, 5).toInt(16),
            coldBlueString.substring(5, 7).toInt(16),
            coldBlueString.substring(7, 9).toInt(16)
        )
        val hotPink = intArrayOf(
            hotPinkString.substring(3, 5).toInt(16),
            hotPinkString.substring(5, 7).toInt(16),
            hotPinkString.substring(7, 9).toInt(16)
        )
        val r = (coldBlue[0] + (hotPink[0] - coldBlue[0]) * offset).toInt()
        val g = (coldBlue[1] + (hotPink[1] - coldBlue[1]) * offset).toInt()
        val b = (coldBlue[2] + (hotPink[2] - coldBlue[2]) * offset).toInt()
        return Color.argb(255, r, g, b)
    }
}