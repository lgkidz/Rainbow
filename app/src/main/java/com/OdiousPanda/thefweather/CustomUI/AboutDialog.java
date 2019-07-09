package com.OdiousPanda.thefweather.CustomUI;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.OdiousPanda.thefweather.R;

public class AboutDialog {
    public void showDialog(Context context){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.about_me_dialog);

        Button close = dialog.findViewById(R.id.btn_close_about);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
