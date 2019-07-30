package com.OdiousPanda.rainbow.Utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.OdiousPanda.rainbow.API.Constant;

public class MyTextUtil {
    private static final String referralString = "?utm_source=rainbow&utm_medium=referral";
    public static SpannableStringBuilder getReferralHtml(Context context,String profileUrl, String profileName){
        String html = "Photo by <a href='";
        html += profileUrl + referralString;
        html+="'>";
        html+= profileName;
        html+= "</a> on <a href='";
        html+= Constant.UNSPLASH_HOME_URL;
        html+="'> Unsplash";
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(context,strBuilder, span);
        }
        return strBuilder;
    }

    private static void makeLinkClickable(final Context context, SpannableStringBuilder strBuilder, final URLSpan span)
    {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                Log.d("loglog", span.getURL());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(span.getURL()));
                context.startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
                //ds.setColor(Color.rgb(146,215,237));
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }
}
