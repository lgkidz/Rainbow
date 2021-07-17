package com.odiousPanda.rainbowKt.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.odiousPanda.rainbowKt.R
import com.odiousPanda.rainbowKt.apis.APIConstants
import java.util.*

object TextUtil {
    private const val referralString = "?utm_source=rainbow&utm_medium=referral"

    fun getReferralHtml(
        context: Context,
        profileUrl: String,
        profileName: String?
    ): SpannableStringBuilder? {
        val html = "${context.getString(R.string.photo_by)} " +
                "<a href='$profileUrl$referralString'>$profileName</a> " +
                "${context.getString(R.string.on)} " +
                "<a href='${APIConstants.UNSPLASH_HOME_URL}$referralString}'> Unsplash</a>"

        val sequence: CharSequence = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val strBuilder = SpannableStringBuilder(sequence)
        val urls =
            strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
        for (span in urls) {
            makeLinkClickable(context, strBuilder, span)
        }
        return strBuilder
    }

    private fun makeLinkClickable(
        context: Context,
        strBuilder: SpannableStringBuilder,
        span: URLSpan
    ) {
        val start = strBuilder.getSpanStart(span)
        val end = strBuilder.getSpanEnd(span)
        val flags = strBuilder.getSpanFlags(span)
        val clickable: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(span.url))
                context.startActivity(browserIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(context, R.color.hyperBlue)
                ds.isUnderlineText = true
            }
        }
        strBuilder.setSpan(clickable, start, end, flags)
        strBuilder.removeSpan(span)
    }

    fun getTimeStringPretty(h: Int, m: Int): String? {
        var hTemp = h
        var AP = " AM"
        val minute = if (m < 10) "0$m" else m.toString()
        if (hTemp >= 12) {
            AP = " PM"
            if (hTemp >= 13) {
                hTemp -= 12
            }
        }
        val hour = hTemp.toString()
        return "$hour:$minute$AP"
    }

    fun locationStringForNearbySearch(lat: String, lon: String): String {
        return "$lat,$lon"
    }

    fun getNotificationText(
        context: Context,
        temp: Float,
        summary: String,
        precipitationType: String,
        precipitationProb: Float
    ): String {
        var notificationText = summary
        val precipitationProbText = "${(100 * precipitationProb).toInt()}%"
        val additionalComment: String
        var precipitationText = " ${context.getString(R.string.there_will_be)} "
        precipitationText += "$precipitationProbText ${context.getString(R.string.chance_of)} $precipitationType."
        if (precipitationType == "rain") {
            precipitationText += if (precipitationProb < 0.5) "" else " ${context.getString(R.string.bring_umbrella)}"
        }
        val tempC = UnitConverter.toCelsius(temp)
        additionalComment = when {
            tempC < Constants.APT_TEMP_COLD -> {
                val coldComments =
                    context.resources.getStringArray(R.array.cold_notification_comment)

                " ${coldComments[Random().nextInt(coldComments.size)]}"
            }
            tempC > Constants.APT_TEMP_HOT -> {
                val hotComments =
                    context.resources.getStringArray(R.array.hot_notification_comment)

                " ${hotComments[Random().nextInt(hotComments.size)]}"
            }
            else -> {
                val randomComments =
                    context.resources.getStringArray(R.array.random_notification_comment)

                " ${randomComments[Random().nextInt(randomComments.size)]}"
            }
        }
        notificationText += "$precipitationText$additionalComment"
        return notificationText
    }

    fun capitalizeSentence(sentence: String): String {
        val sentenceTmp = sentence.toLowerCase(Locale.ROOT)
        val result = StringBuilder()
        var capitalize = true
        for (c in sentenceTmp.toCharArray()) {
            if (capitalize) {
                result.append(Character.toUpperCase(c))
                if (!Character.isWhitespace(c) && c != '.') {
                    capitalize = false
                }
            } else {
                result.append(c)
                if (c == '.') {
                    capitalize = true
                }
            }
        }
        return result.toString()
    }
}