package com.odiousPanda.rainbowKt.utilities

import com.odiousPanda.rainbowKt.R
import java.lang.reflect.Field
import java.util.*

class FoodIconUtil {
    private val foodIcons = mutableListOf<Field>()

    init {
        val drawablesFields: Array<Field> = R.drawable::class.java.fields
        for (field in drawablesFields) {
            try {
                if (field.name.contains("dish") || field.name
                        .contains("dessert") || field.name.contains("drink")
                ) {
                    foodIcons.add(field)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRandomIcons(): Int {
        try {
            return foodIcons[Random().nextInt(foodIcons.size)].getInt(null)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return R.drawable.dish_ic_hamburger
    }
}