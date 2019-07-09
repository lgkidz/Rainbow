package com.OdiousPanda.thefweather.CustomUI;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.OdiousPanda.thefweather.DataModel.AQI.AirQuality;
import com.OdiousPanda.thefweather.R;

public class AirQualityDialog {
    private AirQuality airQuality;
    private LinearLayout airQualityIndexScale;
    private ImageView aqiIndexIndicator;
    Dialog dialog;

    public AirQualityDialog(final Context context, AirQuality airQuality){
        this.airQuality = airQuality;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.air_quality_detailed_dialog);

        final float aqiIndex = airQuality.getData().aqi;

        TextView tvAqiIndex = dialog.findViewById(R.id.tv_aqi_index);
        TextView tvAqiLevel = dialog.findViewById(R.id.tv_aqi_level);
        TextView tvAqiDes = dialog.findViewById(R.id.tv_aqi_des);
        aqiIndexIndicator = dialog.findViewById(R.id.aqi_index_indicator);
        airQualityIndexScale = dialog.findViewById(R.id.index_scale);

        tvAqiIndex.setText(String.valueOf((int)aqiIndex));

        if (aqiIndex <= 50) {
            tvAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_good));
            tvAqiLevel.setText(context.getString(R.string.aqi_good));
            tvAqiDes.setText(context.getString(R.string.aqi_good_des));
        } else if (aqiIndex > 50 && aqiIndex <= 100) {
            tvAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_moderate));
            tvAqiLevel.setText(context.getString(R.string.aqi_moderate));
            tvAqiDes.setText(context.getString(R.string.aqi_moderate_des));
        } else if (aqiIndex > 100 && aqiIndex <= 150) {
            tvAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_unhealthy_sensitive));
            tvAqiLevel.setText(context.getString(R.string.aqi_unhealthy_sensitive));
            tvAqiDes.setText(context.getString(R.string.aqi_unhealthy_sensitive_des));
        } else if (aqiIndex > 150 && aqiIndex <= 200) {
            tvAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_unhealthy));
            tvAqiLevel.setText(context.getString(R.string.aqi_unhealthy));
            tvAqiDes.setText(context.getString(R.string.aqi_unhealthy_des));
        } else if (aqiIndex > 200 && aqiIndex <= 300) {
            tvAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_very_unhealthy));
            tvAqiLevel.setText(context.getString(R.string.aqi_very_unhealthy));
            tvAqiDes.setText(context.getString(R.string.aqi_very_unhealthy_des));
        } else if (aqiIndex > 300 && aqiIndex <= 500) {
            tvAqiIndex.setTextColor(ContextCompat.getColor(context,R.color.aqi_hazardous));
            tvAqiLevel.setText(context.getString(R.string.aqi_hazardous));
            tvAqiDes.setText(context.getString(R.string.aqi_hazardous_des));
        }

        airQualityIndexScale.post(new Runnable() {
            public void run() {
                try {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) aqiIndexIndicator.getLayoutParams();
                    float scaleWidth = (float) airQualityIndexScale.getWidth();
                    float leftMargin = aqiIndex / 500 * scaleWidth;
                    params.leftMargin = (int) leftMargin;
                    aqiIndexIndicator.setLayoutParams(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        Button close = dialog.findViewById(R.id.btn_close_about);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showDialog(){
        dialog.show();
    }
}
