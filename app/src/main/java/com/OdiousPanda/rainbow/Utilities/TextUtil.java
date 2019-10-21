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
import android.view.View;

import androidx.annotation.NonNull;

import com.OdiousPanda.rainbow.API.APIConstants;
import com.OdiousPanda.rainbow.R;

import java.util.Random;

public class TextUtil {
    private static final String referralString = "?utm_source=rainbow&utm_medium=referral";

    public static SpannableStringBuilder getReferralHtml(Context context, String profileUrl, String profileName) {
        String html = context.getString(R.string.photo_by) + " <a href='";
        html += profileUrl + referralString;
        html += "'>";
        html += profileName;
        html += "</a> " + context.getString(R.string.on) + " <a href='";
        html += APIConstants.UNSPLASH_HOME_URL + referralString;
        html += "'> Unsplash</a>";
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(context, strBuilder, span);
        }
        return strBuilder;
    }

    private static void makeLinkClickable(final Context context, SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(@NonNull View view) {
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

    public static String getTimeStringPretty(int h, int m) {
        String AP = " AM";
        String minute = m < 10 ? "0" + m : String.valueOf(m);
        if (h >= 12) {
            AP = " PM";
            if (h >= 13) {
                h -= 12;
            }
        }
        String hour = String.valueOf(h);
        return hour + ":" + minute + AP;
    }

    public static String locationStringForNearbySearch(String lat, String lon) {
        return lat + "," + lon;
    }

    static String getNotificationText(Context context, float temp, String summary, String precipitationType, float precipitationProb) {
        String notificationText = summary;
        String precipitationProbText = (int) (100 * precipitationProb) + "%";
        String additionalComment;
        String precipitationText = " There will be ";
        if (precipitationType != null) {
            precipitationText += precipitationProbText + " chance of " + precipitationType + ".";
            if (precipitationType.equals("rain")) {
                precipitationText += precipitationProb < 0.5 ? "" : " Bring an umbrella, or a raincoat.";
            }
        } else {
            precipitationText = "";
        }

        if (temp < 16) {
            String[] coldComments = context.getResources().getStringArray(R.array.cold_notification_comment);
            additionalComment = " " + coldComments[new Random().nextInt(coldComments.length)];
        } else if (temp > 30) {
            String[] hotComments = context.getResources().getStringArray(R.array.hot_notification_comment);
            additionalComment = " " + hotComments[new Random().nextInt(hotComments.length)];
        } else {
            String[] randomComments = context.getResources().getStringArray(R.array.random_notification_comment);
            additionalComment = " " + randomComments[new Random().nextInt(randomComments.length)];
        }

        notificationText += precipitationText + additionalComment;
        return notificationText;
    }

    public static String capitalizeSentence(String sentence) {
        sentence = sentence.toLowerCase();
        StringBuilder result = new StringBuilder();
        boolean capitalize = true;
        for (char c : sentence.toCharArray()) {
            if (capitalize) {
                result.append(Character.toUpperCase(c));
                if (!Character.isWhitespace(c) && c != '.') {
                    capitalize = false;
                }
            } else {
                result.append(c);
                if (c == '.') {
                    capitalize = true;
                }
            }
        }
        return result.toString();
    }
}
