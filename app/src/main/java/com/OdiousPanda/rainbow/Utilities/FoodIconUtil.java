package com.OdiousPanda.rainbow.Utilities;

import com.OdiousPanda.rainbow.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FoodIconUtil {
    private List<Field> foodIcons = new ArrayList<>();

    public FoodIconUtil() {
        Field[] drawablesFields = com.OdiousPanda.rainbow.R.drawable.class.getFields();
        for (Field field : drawablesFields) {
            try {
                if (field.getName().contains("dish") || field.getName().contains("dessert") || field.getName().contains("drink")) {
                    foodIcons.add(field);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getRandomIcons() {
        try {
            return foodIcons.get(new Random().nextInt(foodIcons.size())).getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return R.drawable.dish_ic_hamburger;
    }

}
