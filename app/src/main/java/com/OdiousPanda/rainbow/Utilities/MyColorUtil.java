package com.OdiousPanda.rainbow.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.OdiousPanda.rainbow.R;

import java.util.Random;

public class MyColorUtil {

    public static int[] randomColorCode() {
        return new int[]{255, new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)};
    }

    public static int blackOrWhiteOf(int[] argb) {
        int r = argb[1];
        int g = argb[2];
        int b = argb[3];

        if ((r * 0.299 + g * 0.587 + b * 0.114) > 186) {
            return Color.argb(255, 0, 0, 0);
        }

        return Color.argb(255, 255, 255, 255);
    }

    public static int blackOrWhiteOf(int color) {
        String hexColor = String.format("#%06X", (0xFFFFFF & color));
        Log.d("loglog", "blackOrWhiteOf: " + hexColor);
        int[] rgb = new int[]{Integer.parseInt(hexColor.substring(1, 3), 16),
                Integer.parseInt(hexColor.substring(3, 5), 16),
                Integer.parseInt(hexColor.substring(5, 7), 16)};
        int r = rgb[0];
        int g = rgb[1];
        int b = rgb[2];

        if ((r * 0.299 + g * 0.587 + b * 0.114) > 186) {
            return Color.argb(255, 0, 0, 0);
        }

        return Color.argb(255, 255, 255, 255);
    }

    public static int invertColor(int[] argb) {
        int r = 255 - argb[1];
        int g = 255 - argb[2];
        int b = 255 - argb[3];

        return Color.argb(255, r, g, b);
    }

    @SuppressLint("ResourceType")
    public static int getTemperaturePointerColor(Context context, float offset) {
        String coldBlueString = context.getResources().getString(R.color.coldBlue);
        String hotPinkString = context.getResources().getString(R.color.hotPink);
        int[] coldBlue = new int[]{Integer.parseInt(coldBlueString.substring(3, 5), 16),
                Integer.parseInt(coldBlueString.substring(5, 7), 16),
                Integer.parseInt(coldBlueString.substring(7, 9), 16)};
        int[] hotPink = new int[]{Integer.parseInt(hotPinkString.substring(3, 5), 16),
                Integer.parseInt(hotPinkString.substring(5, 7), 16),
                Integer.parseInt(hotPinkString.substring(7, 9), 16)};
        int r = (int) (coldBlue[0] + (hotPink[0] - coldBlue[0]) * offset);
        int g = (int) (coldBlue[1] + (hotPink[1] - coldBlue[1]) * offset);
        int b = (int) (coldBlue[2] + (hotPink[2] - coldBlue[2]) * offset);
        return Color.argb(255, r, g, b);
    }

}
