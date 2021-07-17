package com.odiousPanda.rainbowKt.widgets

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Geocoder
import android.os.Build
import android.os.Handler
import android.os.Message
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.PermissionChecker
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.odiousPanda.rainbowKt.R
import com.odiousPanda.rainbowKt.activities.MainActivity
import com.odiousPanda.rainbowKt.apis.RetrofitService
import com.odiousPanda.rainbowKt.apis.WeatherCall
import com.odiousPanda.rainbowKt.model.dataSource.Quote
import com.odiousPanda.rainbowKt.model.dataSource.weather.Weather
import com.odiousPanda.rainbowKt.utilities.PreferencesUtil
import com.odiousPanda.rainbowKt.utilities.QuoteGenerator
import com.odiousPanda.rainbowKt.utilities.UnitConverter
import kotlinx.coroutines.*
import java.util.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class NormalWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_UPDATE = "actionUpdate"
        const val ACTION_TAP = "widgetTap"

        private const val TEMP_BITMAP = "tempBitmap"
        private const val RF_BITMAP = "RFBitmap"
        private const val MAIN_BITMAP = "mainBitmap"
        private const val SUB_BITMAP = "subBitmap"
        private const val LOCATION_BITMAP = "locationBitmap"
        private const val DOUBLE_CLICK_DELAY = 500L
        private lateinit var weather: Weather
        private val quotes = mutableListOf<Quote>()
        private lateinit var quote: Quote
        private var widgetId: Int = 0
        private lateinit var remoteViews: RemoteViews
        private lateinit var aWm: AppWidgetManager

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.normal_widget)
            widgetId = appWidgetId
            remoteViews = views
            aWm = appWidgetManager

            if (PreferencesUtil.getAppOpenCount(context) > 0) {
                val tapIntent = Intent(context, NormalWidget::class.java)
                tapIntent.action = ACTION_TAP
                val tapPending = PendingIntent.getBroadcast(context, 0, tapIntent, 0)
                remoteViews.setOnClickPendingIntent(R.id.widget_quote_layout, tapPending)
                val toDetailsScreenIntent = Intent(context, MainActivity::class.java)
                toDetailsScreenIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                val pendingIntent =
                    PendingIntent.getActivity(context, widgetId, toDetailsScreenIntent, 0)
                remoteViews.setOnClickPendingIntent(R.id.layout_data, pendingIntent)
                PreferencesUtil.setWidgetTapCount(context, 0)
                aWm.updateAppWidget(widgetId, remoteViews)
                updateData(context)
            }
        }

        private fun updateData(context: Context) {
            val currentTempUnit = PreferencesUtil.getTemperatureUnit(context).toString()
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context)
            if (PermissionChecker.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                return
            }
            val call: WeatherCall = RetrofitService.createWeatherCall()
            fusedLocationClient.lastLocation
                .addOnSuccessListener(OnSuccessListener { location ->
                    if (location == null) {
                        return@OnSuccessListener
                    }
                    var locale =
                        context.resources.configuration.locale.language
                    if (locale != "vi") {
                        locale = "en"
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        val response = call.getWeather(location.latitude.toString(),location.longitude.toString(),locale)

                        if (response.isSuccessful) {
                            weather = response.body()!!
                            val temp: String = UnitConverter.convertToTemperatureUnit(
                                weather.currently.temperature,
                                currentTempUnit
                            )
                            val realFeelTemp =
                                context.resources.getString(R.string.widget_feel_like) + " " + UnitConverter.convertToTemperatureUnit(
                                    weather.currently
                                        .apparentTemperature,
                                    currentTempUnit
                                )
                            remoteViews.setImageViewBitmap(
                                R.id.tv_temp_widget,
                                textAsBitmap(
                                    context,
                                    temp,
                                    TEMP_BITMAP
                                )
                            )
                            remoteViews.setImageViewBitmap(
                                R.id.tv_reaFeel_widget,
                                textAsBitmap(
                                    context,
                                    realFeelTemp,
                                    RF_BITMAP
                                )
                            )
                            val iconName: String =
                                weather.currently.icon.replace("-", "_")
                            val iconResourceId = context.resources.getIdentifier(
                                "drawable/widget_" + iconName + "_w",
                                null,
                                context.packageName
                            )
                            remoteViews.setImageViewResource(
                                R.id.widget_icon,
                                iconResourceId
                            )
                            queryQuotes(context)
                        } else {
                            remoteViews.setViewVisibility(
                                R.id.widget_loading_layout,
                                View.INVISIBLE
                            )
                            aWm.updateAppWidget(
                                widgetId,
                                remoteViews
                            )
                        }
                    }
                    try {
                        val geo = Geocoder(context, Locale.getDefault())
                        val addresses =
                            geo.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0].getAddressLine(0)
                            val addressPieces =
                                address.split(",".toRegex()).toTypedArray()
                            val locationName: String
                            locationName = if (addressPieces.size >= 3) {
                                addressPieces[addressPieces.size - 3].trim { it <= ' ' }
                            } else {
                                addressPieces[addressPieces.size - 2].trim { it <= ' ' }
                            }
                            remoteViews.setImageViewBitmap(
                                R.id.widget_location,
                                textAsBitmap(
                                    context,
                                    locationName,
                                    LOCATION_BITMAP
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        remoteViews.setImageViewBitmap(
                            R.id.widget_location,
                            textAsBitmap(
                                context,
                                context.resources.getString(R.string.currentLocation),
                                LOCATION_BITMAP
                            )
                        )
                    }
                })
        }

        private fun textAsBitmap(
            context: Context,
            text: String,
            bitmapType: String
        ): Bitmap? {
            val paint =
                Paint(Paint.ANTI_ALIAS_FLAG)
            when (bitmapType) {
                TEMP_BITMAP, MAIN_BITMAP -> {
                    paint.textSize = context.resources.getDimension(R.dimen.text_view_34dp)
                }
                RF_BITMAP, LOCATION_BITMAP -> {
                    paint.textSize = context.resources.getDimension(R.dimen.text_view_14dp)
                }
                SUB_BITMAP -> {
                    paint.textSize = context.resources.getDimension(R.dimen.text_view_18dp)
                }
            }
            val typeFace = ResourcesCompat.getFont(context, R.font.montserrat)
            paint.typeface = typeFace
            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.LEFT
            if (bitmapType == MAIN_BITMAP) {
                val mAppWidgetOptions = AppWidgetManager.getInstance(
                    context
                ).getAppWidgetOptions(widgetId)
                val mWidgetPortHeight = mAppWidgetOptions
                    .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
                val mWidgetPortWidth = mAppWidgetOptions
                    .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                val textPaint = TextPaint()
                textPaint.textSize = context.resources.getDimension(R.dimen.text_view_34dp)
                if (mWidgetPortHeight * context.resources
                        .displayMetrics.density + 0.5f < context.resources
                        .getDimension(R.dimen.widget_height) * 3
                ) {
                    textPaint.textSize = context.resources.getDimension(R.dimen.text_view_32dp)
                }
                when {
                    text.length < 70 -> {
                        textPaint.textSize = context.resources.getDimension(R.dimen.text_view_32dp)
                    }
                    text.length < 90 -> {
                        textPaint.textSize = context.resources.getDimension(R.dimen.text_view_28dp)
                    }
                    else -> {
                        textPaint.textSize = context.resources.getDimension(R.dimen.text_view_22dp)
                    }
                }
                textPaint.color = Color.WHITE
                textPaint.typeface = typeFace
                var width = (mWidgetPortWidth * context.resources
                    .displayMetrics.density + 0.5f).toInt()
                if (width <= 0) {
                    width = context.resources.getDimension(R.dimen.widget_width).toInt()
                }
                val staticLayout: StaticLayout
                staticLayout = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    val builder =
                        StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
                            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                            .setLineSpacing(0.0f, 1.0f)
                            .setIncludePad(false)
                    builder.build()
                } else {
                    StaticLayout(
                        text,
                        textPaint,
                        width,
                        Layout.Alignment.ALIGN_NORMAL,
                        1.0f,
                        0.0f,
                        false
                    )
                }
                val height = staticLayout.height
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                staticLayout.draw(canvas)
                return bitmap
            }
            val baseline = -paint.ascent() // ascent() is negative
            val width = (paint.measureText(text) + 1f).toInt() // round
            val height = (baseline + paint.descent() + 1f).toInt()
            val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            canvas.drawText(text, 0f, baseline, paint)
            return image
        }

        private fun queryQuotes(context: Context) {
            val collection = if (context.resources
                    .configuration.locale.language == "vi"
            ) "quotes-vi" else "quotes"
            FirebaseFirestore.getInstance().collection(collection)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val q = document.toObject(Quote::class.java)
                            quotes.add(q)
                        }
                    } else {
                        remoteViews.setViewVisibility(
                            R.id.widget_loading_layout,
                            View.INVISIBLE
                        )
                        aWm.updateAppWidget(
                            widgetId,
                            remoteViews
                        )
                    }
                    getQuote(context, weather)
                }.addOnFailureListener {
                    remoteViews.setViewVisibility(
                        R.id.widget_loading_layout,
                        View.INVISIBLE
                    )
                    aWm.updateAppWidget(
                        widgetId,
                        remoteViews
                    )
                }
        }

        private fun getQuote(context: Context, w: Weather) {
            weather = w
            if (quotes.size == 0) {
                queryQuotes(context)
                return
            }
            val isExplicit = PreferencesUtil.isExplicit(context)
            val weatherQuotes: List<Quote> =
                QuoteGenerator.filterQuotes(weather, quotes, isExplicit, true)
            quote = Quote()
            if (weatherQuotes.isNotEmpty()) {
                quote = weatherQuotes[Random().nextInt(weatherQuotes.size)]
            } else {
                quote.setDefaultQuote()
            }

            remoteViews.setImageViewBitmap(
                R.id.quote_main,
                textAsBitmap(
                    context,
                    quote.main,
                    MAIN_BITMAP
                )
            )
            remoteViews.setImageViewBitmap(
                R.id.quote_sub,
                textAsBitmap(context, quote.sub, SUB_BITMAP)
            )
            remoteViews.setViewVisibility(
                R.id.widget_loading_layout,
                View.INVISIBLE
            )
            aWm.updateAppWidget(widgetId, remoteViews)
        }
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        if (appWidgetIds != null) {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context!!, appWidgetManager!!, appWidgetId)
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        aWm = AppWidgetManager.getInstance(context)
        val ids = aWm.getAppWidgetIds(
            ComponentName(
                context!!,
                NormalWidget::class.java
            )
        )
        if (ids.isNotEmpty()) {
            widgetId = ids[ids.size - 1]
            remoteViews = RemoteViews(context.packageName, R.layout.normal_widget)
            super.onReceive(context, intent)
            if (ACTION_UPDATE == intent!!.action) {
                updateData(context)
            }
            if ("com.sec.android.widgetapp.APPWIDGET_RESIZE" == intent.action) {
                remoteViews.setImageViewBitmap(
                    R.id.quote_main,
                    textAsBitmap(
                        context,
                        quote.main,
                        MAIN_BITMAP
                    )
                )
                aWm.updateAppWidget(
                    widgetId,
                    remoteViews
                )
            }

            if (ACTION_TAP == intent.action) {
                var clickCount = PreferencesUtil.getWidgetTapCount(context)
                PreferencesUtil.setWidgetTapCount(context, ++clickCount)
                @SuppressLint("HandlerLeak") val handler: Handler =
                    object : Handler() {
                        override fun handleMessage(msg: Message) {
                            val clickCountTemp = PreferencesUtil.getWidgetTapCount(context)
                            if (clickCountTemp > 1) {
                                remoteViews.setViewVisibility(
                                    R.id.widget_loading_layout,
                                    View.VISIBLE
                                )
                                aWm.updateAppWidget(
                                    widgetId,
                                    remoteViews
                                )
                                updateData(context)
                            }
                            PreferencesUtil.setWidgetTapCount(context, 0)
                        }
                    }
                if (clickCount == 1) {
                    object : Thread() {
                        override fun run() {
                            try {
                                synchronized(
                                    this
                                ) { sleep(DOUBLE_CLICK_DELAY) }
                                handler.sendEmptyMessage(0)
                            } catch (ex: InterruptedException) {
                                ex.printStackTrace()
                            }
                        }
                    }.start()
                }
            }
        }
    }
}