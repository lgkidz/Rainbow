package com.odiousPanda.rainbowKt.utilities

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.odiousPanda.rainbowKt.model.dataSource.Quote
import com.odiousPanda.rainbowKt.model.dataSource.weather.Weather
import java.util.*

class QuoteGenerator(private val context: Context) {
    companion object {
        const val TAG = "weatherA"
        fun filterQuotes(
            weather: Weather,
            allQuotes: List<Quote>,
            isExplicit: Boolean,
            forWidget: Boolean
        ): List<Quote> {
            val weatherQuotes: MutableList<Quote> = mutableListOf()
            val temp =
                UnitConverter.toCelsius(weather.currently.apparentTemperature)
            val summary: String = weather.currently.icon
            val criteria: MutableList<String> =
                mutableListOf()
            when {
                temp > Constants.APT_TEMP_HOT -> {
                    criteria.add("hot")
                }
                temp < Constants.APT_TEMP_COLD -> {
                    criteria.add("cold")
                }
                else -> {
                    criteria.add("cool")
                }
            }
            if (summary.contains("rain")) {
                criteria.add("rain")
            } else if (summary.contains("cloudy")) {
                criteria.add("cloudy")
            } else if (summary.contains("fog")) {
                criteria.add("fog")
            } else if (summary.contains("snow") || summary.contains("sleet")) {
                criteria.add("snow")
            } else if (summary.contains("hail")) {
                criteria.add("hail")
            } else if (summary.contains("thunderstorm")) {
                criteria.add("thunderstorm")
            } else if (summary.contains("tornado")) {
                criteria.add("tornado")
            } else if (summary.contains("clear")) {
                criteria.add("clear")
            }
            for (q in allQuotes) {
                // att * means quotes unrelated to weather (jokes / inspiring quotes,...)
                if (q.att.contains("*")) {
                    if (!forWidget && q.att.contains("widget")) {
                        continue
                    } else {
                        if (isExplicit) {
                            Quote().apply {
                                main = censorOffensiveWords(q.main)
                                sub = censorOffensiveWords(q.sub)
                                att = q.att

                            }.also {
                                weatherQuotes.add(it)
                            }
                        } else {
                            weatherQuotes.add(q)
                        }
                    }
                    continue
                }
                // Quotes related to weather
                for (s in criteria) {
                    //if match one of the criteria
                    if (q.att.contains(s)) {
                        if (isExplicit) {
                            Quote().apply {
                                main = censorOffensiveWords(q.main)
                                sub = censorOffensiveWords(q.sub)
                                att = q.att
                            }.also {
                                weatherQuotes.add(it)
                            }
                        }
                        weatherQuotes.add(q)
                        break
                    }
                }
            }
            return weatherQuotes
        }

        private fun censorOffensiveWords(text: String): String {
            var tempText = text
            val alternativesForFucking = arrayOf(
//                "frickin’ ",
//                "freakin’ ",
//                "freaking ",
//                "flippin’ ",
//                "flipping ",
//                "fricking ",
                ""
            )
            val alternativesForDamn =
                arrayOf("darn", "dang")
            val alternativesForShit =
                arrayOf("crap", "crud")
            val alternativesForHell = arrayOf("heck")
            val alternativesForAss =
                arrayOf("arse ", "butt ", "bum ")
            val alternativesForVl =
                arrayOf("lắm", "thật", "kinh", "")
            val alternativesForVcl =
                arrayOf("lắm", "thật", "kinh")
            val alternativesForEo = arrayOf("không")
            val alternativesForDeo = arrayOf("không")
            tempText = tempText.toLowerCase(Locale.ROOT).replace(
                "damn",
                alternativesForDamn[Random().nextInt(alternativesForDamn.size)]
            )
            tempText = tempText.replace(
                "shit",
                alternativesForShit[Random().nextInt(alternativesForShit.size)]
            )
            tempText = tempText.replace(
                "hell",
                alternativesForHell[Random().nextInt(alternativesForHell.size)]
            )
            tempText = tempText.replace(
                "ass ",
                alternativesForAss[Random().nextInt(alternativesForAss.size)]
            )
            tempText = tempText.replace(
                "fucking ",
                alternativesForFucking[Random().nextInt(alternativesForFucking.size)]
            )
            tempText = tempText.replace(
                "vl",
                alternativesForVl[Random().nextInt(alternativesForVl.size)]
            )
            tempText = tempText.replace(
                "vcl",
                alternativesForVcl[Random().nextInt(alternativesForVcl.size)]
            )
            tempText = tempText.replace(
                "éo",
                alternativesForEo[Random().nextInt(alternativesForEo.size)]
            )
            tempText = tempText.replace(
                "đéo",
                alternativesForDeo[Random().nextInt(alternativesForDeo.size)]
            )
            val textNoStrongWords = tempText.replace("i ", "I ").trim()
            return if (textNoStrongWords.isNotEmpty()) {
                TextUtil.capitalizeSentence(textNoStrongWords)
            } else TextUtil.capitalizeSentence(text)
        }
    }

    private val db = FirebaseFirestore.getInstance()
    private val quotes = mutableListOf<Quote>()
    private var weather: Weather? = null
    private var listener: UpdateScreenQuoteListener? = null

    fun setUpdateScreenQuoteListener(listener: UpdateScreenQuoteListener) {
        this.listener = listener
    }

    private fun queryQuote() {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].language
        } else {
            context.resources.configuration.locale.language
        }
        val collection = if (locale == "vi"
        ) "quotes-vi" else "quotes"

        db.collection(collection)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        quotes.add(document.toObject(Quote::class.java))
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", it.exception)
                }

                weather?.let { weather -> updateHomeScreenQuote(weather) }
            }
    }

    fun updateHomeScreenQuote(weather: Weather) {
        this.weather = weather
        if (quotes.size == 0) {
            queryQuote()
            return
        }

        val isExplicit = PreferencesUtil.isExplicit(context)
        val weatherQuotes = filterQuotes(weather, quotes, isExplicit, false)
        var randomQuote = Quote("", "", "")
        if (weatherQuotes.isNotEmpty()) {
            randomQuote = weatherQuotes[Random().nextInt(weatherQuotes.size)]
            if (randomQuote.main.isEmpty() && randomQuote.sub.isEmpty()) {
                randomQuote.setDefaultQuote()
            }
        } else {
            randomQuote.setDefaultQuote()
        }
        listener?.updateScreenQuote(randomQuote)
    }

    interface UpdateScreenQuoteListener {
        fun updateScreenQuote(randomQuote: Quote)
    }
}