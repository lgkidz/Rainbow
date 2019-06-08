package com.OdiousPanda.thefweather.Utilities;

import android.graphics.Color;

import java.util.Random;

public class ColorUtil {

    public static int[] randomColorCode(){
        String letters = "0123456798ABCDEF";
        String color = "#";

        for(int i = 0;i < 6; i++){
            color += letters.charAt((int)Math.floor(Math.random() * letters.length()));
        }

        return new int[]{255,new Random().nextInt(256),new Random().nextInt(256),new Random().nextInt(256)};
    }

    public static int invertColor(int[] argb){
        int r = argb[1];
        int g = argb[2];
        int b = argb[3];

        if((r * 0.299 + g * 0.587 + b * 0.114) > 186){
            return Color.argb(255,0,0,0);
        }

        return Color.argb(255,255,255,255);
    }

}
