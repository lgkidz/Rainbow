package com.odiousPanda.rainbowKt.mainFragments

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import com.odiousPanda.rainbowKt.R
import com.odiousPanda.rainbowKt.adapters.HourlyForecastAdapter
import com.odiousPanda.rainbowKt.databinding.FragmentDetailsBinding
import com.odiousPanda.rainbowKt.model.dataSource.Nearby.NearbySearch
import com.odiousPanda.rainbowKt.model.dataSource.aqi.Aqi
import com.odiousPanda.rainbowKt.model.dataSource.weather.Weather
import com.odiousPanda.rainbowKt.utilities.*
import com.odiousPanda.rainbowKt.viewModels.WeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.text.DateFormat
import kotlin.math.PI

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class DetailsFragment : Fragment() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: FragmentDetailsBinding

    var aqiMoreDetailsShowing = false
    private lateinit var aqiMoreDetailsSlideUp: SlideUp
    private val foodIconUtil = FoodIconUtil()
    private val clothesIconUtil = ClothesIconUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedLocationData.observe(viewLifecycleOwner, Observer {
            it.weather?.let {  weather ->
                updateRealFeelData(weather)
                updateHourlyForecastData(weather)
                updateCloudCoverData(weather)
                updatePressureData(weather)
                updateVisibilityData(weather)
                updateWindData(weather)
                updateSunData(weather)
            }
            it.airQuality?.let { aqi ->
                updateAqi(aqi)
            }
        })
        
        viewModel.currentTempUnit.observe(viewLifecycleOwner, Observer {
            updateTempUnit()
        })

        viewModel.currentDistanceUnit.observe(viewLifecycleOwner, Observer {
            updateDistanceUnit()
        })

        viewModel.currentSpeedUnit.observe(viewLifecycleOwner, Observer {
            updateSpeedUnit()
        })

        viewModel.currentPressureUnit.observe(viewLifecycleOwner, Observer {
            updatePressureUnit()
        })

        viewModel.isExplicit.observe(viewLifecycleOwner, Observer {
            updateExplicitSetting()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)

        initViews()
        return binding.root
    }

    private fun initViews() {
        aqiMoreDetailsSlideUp = SlideUpBuilder(binding.aqiMoreLayout.root)
            .withStartState(SlideUp.State.HIDDEN)
            .withStartGravity(Gravity.BOTTOM)
            .withListeners(object : SlideUp.Listener.Events {
                override fun onVisibilityChanged(visibility: Int) {}

                override fun onSlide(percent: Float) {
                    aqiMoreDetailsShowing = percent != 100f
                }

            })
            .build()

        binding.aqiDetailLayout.iconAqiInfo.setOnClickListener {
            showAqiMoreDetailsDialog()
        }
        binding.aqiMoreLayout.btnCloseAqiDetails.setOnClickListener {
            closeAqiMoreDetailsDialog()
        }

        binding.hourlyForecastRv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        binding.poweredByDarkSky.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.powered_dark_sky_link)))
            startActivity(browserIntent)
        }

    }

    private fun updateRealFeelData(weather: Weather){
        val isExplicit = PreferencesUtil.isExplicit(requireContext())
        val realFeelPreFix = if(isExplicit) getString(R.string.real_feel_title_explicit) else getString(R.string.real_feel_title)
        val realFeelText = "$realFeelPreFix ${UnitConverter.convertToTemperatureUnitClean(
            weather.currently.apparentTemperature,
            PreferencesUtil.getTemperatureUnit(requireContext()).toString()
        )}"
        binding.tvTempRealFeel.text = realFeelText

        clothesIconUtil.generateCriteria(weather)
        binding.icHeadWear.setImageResource(clothesIconUtil.getHeadIcon())
        binding.icUpperBody.setImageResource(clothesIconUtil.getUpperIcon())
        binding.icLowerBody.setImageResource(clothesIconUtil.getLowerIcon())
        binding.icFootWare.setImageResource(clothesIconUtil.getFootIcon())
        binding.icHand.setImageResource(clothesIconUtil.getHandIcon())
        binding.tvWearCause.text = clothesIconUtil.getCause(requireContext())
    }

    fun updateNearbySearchData(nearbySearch: NearbySearch){
        if(nearbySearch.results.isNotEmpty()){
            binding.icFoodType.setImageResource(foodIconUtil.getRandomIcons())
            val currentPlaceName = binding.food.text.toString()
            val placeName: String

            while (true) {
                val position = (0 until nearbySearch.results.size).random()
                if(!nearbySearch.results[position].name.equals(currentPlaceName, ignoreCase = true)){
                    placeName = nearbySearch.results[position].name
                    break
                }
            }
            binding.food.apply {
                text = SpannableString(placeName).apply {
                    setSpan(UnderlineSpan(), 0, this.length, 0)
                }
                setOnClickListener {
                    val toMapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$placeName"))
                    startActivity(toMapIntent)
                }
            }

            binding.icRefreshFood.setOnClickListener {
                val spin = AnimationUtils.loadAnimation(requireContext(), R.anim.quick_spin)
                it.startAnimation(spin)
                if (nearbySearch.results.isNotEmpty()) {
                    updateNearbySearchData(nearbySearch)
                }
            }
        }
    }

    private fun updateTempUnit() {
        viewModel.getWeather()?.let {
            it.weather?.let { weather -> updateHourlyForecastData(weather) }
        }
    }

    private fun updateSpeedUnit() {
        viewModel.getWeather()?.let {
            it.weather?.let { weather -> updateWindData(weather) }
        }
    }

    private fun updateDistanceUnit() {
        viewModel.getWeather()?.let {
            it.weather?.let { weather -> updateVisibilityData(weather) }
        }
    }

    private fun updatePressureUnit() {
        viewModel.getWeather()?.let {
            it.weather?.let { weather -> updatePressureData(weather) }
        }
    }

    private fun updateExplicitSetting() {
        viewModel.getWeather()?.let {
            it.weather?.let { weather -> updateSunData(weather) }
            it.airQuality?.let { aqi -> updateAqi(aqi) }
        }
    }

    private fun updateHourlyForecastData(weather: Weather) {
        val adapter = HourlyForecastAdapter(requireContext(), weather.hourly.data)
        binding.hourlyForecastRv.adapter = adapter
    }

    private fun updateWindData(weather: Weather) {
        binding.windDetailLayout.windSpeed.text = UnitConverter.convertToSpeedUnit(
            weather.currently.windSpeed,
            PreferencesUtil.getSpeedUnit(requireContext()).toString()
        )
        val windSpeedMps = UnitConverter.toMeterPerSecond(weather.currently.windSpeed)
        var windDirectionText = ""
        if (windSpeedMps > 0) {
            val windBearing: Float = weather.currently.windBearing
            if (windBearing >= 0 && windBearing < 11.25 || windBearing in 348.75..360.0) {
                windDirectionText = resources.getString(R.string.wind_north)
            } else if (windBearing >= 11.25 && windBearing < 33.75) {
                windDirectionText =
                    resources.getString(R.string.wind_north) + " - " + resources.getString(R.string.wind_north_east)
            } else if (windBearing >= 33.75 && windBearing < 56.25) {
                windDirectionText = resources.getString(R.string.wind_north_east)
            } else if (windBearing >= 56.25 && windBearing < 78.75) {
                windDirectionText =
                    resources.getString(R.string.wind_east) + " - " + resources.getString(R.string.wind_north_east)
            } else if (windBearing >= 78.75 && windBearing < 101.25) {
                windDirectionText = resources.getString(R.string.wind_east)
            } else if (windBearing >= 101.25 && windBearing < 123.75) {
                windDirectionText =
                    resources.getString(R.string.wind_east) + " - " + resources.getString(R.string.wind_south_east)
            } else if (windBearing >= 123.75 && windBearing < 146.25) {
                windDirectionText = resources.getString(R.string.wind_south_east)
            } else if (windBearing >= 146.25 && windBearing < 168.75) {
                windDirectionText =
                    resources.getString(R.string.wind_south) + " - " + resources.getString(R.string.wind_south_east)
            } else if (windBearing >= 168.75 && windBearing < 191.25) {
                windDirectionText = resources.getString(R.string.wind_south)
            } else if (windBearing >= 191.25 && windBearing < 213.75) {
                windDirectionText =
                    resources.getString(R.string.wind_south) + " - " + resources.getString(R.string.wind_south_west)
            } else if (windBearing >= 213.75 && windBearing < 236.25) {
                windDirectionText = resources.getString(R.string.wind_south_west)
            } else if (windBearing >= 236.25 && windBearing < 258.75) {
                windDirectionText =
                    resources.getString(R.string.wind_west) + " - " + resources.getString(R.string.wind_south_west)
            } else if (windBearing >= 258.75 && windBearing < 281.25) {
                windDirectionText = resources.getString(R.string.wind_west)
            } else if (windBearing >= 281.25 && windBearing < 303.75) {
                windDirectionText =
                    resources.getString(R.string.wind_west) + " - " + resources.getString(R.string.wind_north_west)
            } else if (windBearing in 303.75..326.25) {
                windDirectionText = resources.getString(R.string.wind_north_west)
            } else if (windBearing >= 326.25 && windBearing < 348.75) {
                windDirectionText =
                    resources.getString(R.string.wind_north) + " - " + resources.getString(R.string.wind_north_west)
            }
        }

        binding.windDetailLayout.windDirection.text = windDirectionText

        binding.windDetailLayout.windmillWings.clearAnimation()
        val windmillWingsDiameter = 10 // diameter in meter
        val roundPerSec = windSpeedMps / (PI * windmillWingsDiameter)
        val animationDuration = (1000 / roundPerSec).toLong()
        val rotateAnimation = RotateAnimation(
            0F,
            -360F,
            Animation.RELATIVE_TO_SELF,
            0.5F,
            Animation.RELATIVE_TO_SELF,
            0.5F
        )
            .apply {
                interpolator = LinearInterpolator()
                duration = animationDuration
                repeatCount = Animation.INFINITE
            }
        binding.windDetailLayout.windmillWings.startAnimation(rotateAnimation)
    }

    private fun updateSunData(weather: Weather) {
        val isExplicit = PreferencesUtil.isExplicit(requireContext())
        binding.sunDetailLayout.sunTitle.text =
            if (isExplicit) resources.getString(R.string.sun) else resources.getString(R.string.sun_not_explicit)
        val df = DateFormat.getTimeInstance(DateFormat.SHORT)
        val sunriseTime = weather.daily.data[0].sunriseTime * 1000
        val sunsetTime = weather.daily.data[0].sunsetTime * 1000
        val middayTime = (sunriseTime + sunsetTime) / 2
        binding.sunDetailLayout.sunriseTime.text = df.format(sunriseTime)
        binding.sunDetailLayout.sunsetTime.text = df.format(sunsetTime)
        binding.sunDetailLayout.midDayTime.text = df.format(middayTime)

        val currentTime = System.currentTimeMillis()
        if (currentTime in sunriseTime..sunsetTime) {
            binding.sunDetailLayout.sunBar.post {
                try {
                    val params =
                        binding.sunDetailLayout.timeIndicator.layoutParams as ConstraintLayout.LayoutParams
                    val barHeight = binding.sunDetailLayout.sunBar.height.toFloat()
                    val topMargin =
                        (currentTime - sunriseTime) / (sunsetTime - sunriseTime) * barHeight
                    params.topMargin =
                        topMargin.toInt() - binding.sunDetailLayout.timeIndicator.height / 2
                    binding.sunDetailLayout.timeIndicator.layoutParams = params
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            binding.sunDetailLayout.timeIndicator.visibility = View.GONE
        }
    }

    private fun updatePressureData(weather: Weather) {
        val pressureValue = UnitConverter.convertToPressureUnit(
            requireContext(),
            weather.currently.pressure,
            PreferencesUtil.getPressureUnit(requireContext()).toString()
        )
        binding.cloudPressureVisibilityDetailLayout.tvPressure.text = pressureValue
        if (PreferencesUtil.getPressureUnit(requireContext())
                .toString() == resources.getString(R.string.depression_unit)
        ) {
            binding.cloudPressureVisibilityDetailLayout.tvPressureTitle.text =
                resources.getString(R.string.depression_level_title)
        } else {
            binding.cloudPressureVisibilityDetailLayout.tvPressureTitle.text =
                resources.getString(R.string.pressure_title)
        }
    }

    private fun updateCloudCoverData(weather: Weather) {
        val cloud = "${(weather.currently.cloudCover * 100).toInt()}%"
        binding.cloudPressureVisibilityDetailLayout.cloudCoverValue.text = cloud
    }

    private fun updateVisibilityData(weather: Weather) {
        binding.cloudPressureVisibilityDetailLayout.tvVisibility.text =
            UnitConverter.convertToDistanceUnit(
                weather.currently.visibility,
                PreferencesUtil.getDistanceUnit(requireContext()).toString()
            )
    }

    private fun updateAqi(aqi: Aqi) {
        val aqiValue = aqi.data.aqi
        binding.aqiDetailLayout.tvAqiIndex.text = aqiValue.toInt().toString()
        binding.aqiMoreLayout.aqiMoreIndex.text = aqiValue.toInt().toString()
        updateAqIMainView(aqiValue)
        updateAqiDescription(aqiValue)

        binding.aqiMoreLayout.indexScale.post {
            try {
                val params =
                    binding.aqiMoreLayout.aqiIndexIndicator.layoutParams as ConstraintLayout.LayoutParams
                val scaleWidth = binding.aqiMoreLayout.indexScale.width.toFloat()
                var leftMargin = aqiValue / 500 * scaleWidth
                if (aqiValue > 500) {
                    leftMargin = scaleWidth
                }
                params.leftMargin =
                    (leftMargin - binding.aqiMoreLayout.aqiIndexIndicator.width / 2).toInt()
                binding.aqiMoreLayout.aqiIndexIndicator.layoutParams = params
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun updateAqIMainView(aqi: Float) {
        val context = requireContext()
        val aqiDesBackgroundDrawable =
            ContextCompat.getDrawable(context, R.drawable.aqi_des_background) as GradientDrawable
        when {
            aqi <= 50 -> {
                aqiDesBackgroundDrawable.setStroke(
                    resources.getDimension(R.dimen.aqi_stroke_width).toInt(),
                    ContextCompat.getColor(context, R.color.aqi_good)
                )
                binding.aqiDetailLayout.aqiDesLayout.background = aqiDesBackgroundDrawable
                binding.aqiDetailLayout.tvAqiIndex.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.aqi_good)
                binding.aqiDetailLayout.tvAqiIndex.setTextColor(
                    ColorUtil.blackOrWhiteOf(
                        ContextCompat.getColor(context, R.color.aqi_good)
                    )
                )
                binding.aqiMoreLayout.aqiMoreIndex.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.aqi_good
                    )
                )
            }
            aqi <= 100 -> {
                aqiDesBackgroundDrawable.setStroke(
                    resources.getDimension(R.dimen.aqi_stroke_width).toInt(),
                    ContextCompat.getColor(context, R.color.aqi_moderate)
                )
                binding.aqiDetailLayout.aqiDesLayout.background = aqiDesBackgroundDrawable
                binding.aqiDetailLayout.tvAqiIndex.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.aqi_moderate)
                binding.aqiDetailLayout.tvAqiIndex.setTextColor(
                    ColorUtil.blackOrWhiteOf(
                        ContextCompat.getColor(context, R.color.aqi_moderate)
                    )
                )
                binding.aqiMoreLayout.aqiMoreIndex.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.aqi_moderate
                    )
                )
            }
            aqi <= 150 -> {
                aqiDesBackgroundDrawable.setStroke(
                    resources.getDimension(R.dimen.aqi_stroke_width).toInt(),
                    ContextCompat.getColor(context, R.color.aqi_unhealthy_sensitive)
                )
                binding.aqiDetailLayout.aqiDesLayout.background = aqiDesBackgroundDrawable
                binding.aqiDetailLayout.tvAqiIndex.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.aqi_unhealthy_sensitive)
                binding.aqiDetailLayout.tvAqiIndex.setTextColor(
                    ColorUtil.blackOrWhiteOf(
                        ContextCompat.getColor(context, R.color.aqi_unhealthy_sensitive)
                    )
                )
                binding.aqiMoreLayout.aqiMoreIndex.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.aqi_unhealthy_sensitive
                    )
                )
            }
            aqi <= 200 -> {
                aqiDesBackgroundDrawable.setStroke(
                    resources.getDimension(R.dimen.aqi_stroke_width).toInt(),
                    ContextCompat.getColor(context, R.color.aqi_unhealthy)
                )
                binding.aqiDetailLayout.aqiDesLayout.background = aqiDesBackgroundDrawable
                binding.aqiDetailLayout.tvAqiIndex.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.aqi_unhealthy)
                binding.aqiDetailLayout.tvAqiIndex.setTextColor(
                    ColorUtil.blackOrWhiteOf(
                        ContextCompat.getColor(context, R.color.aqi_unhealthy)
                    )
                )
                binding.aqiMoreLayout.aqiMoreIndex.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.aqi_unhealthy
                    )
                )
            }
            aqi <= 300 -> {
                aqiDesBackgroundDrawable.setStroke(
                    resources.getDimension(R.dimen.aqi_stroke_width).toInt(),
                    ContextCompat.getColor(context, R.color.aqi_very_unhealthy)
                )
                binding.aqiDetailLayout.aqiDesLayout.background = aqiDesBackgroundDrawable
                binding.aqiDetailLayout.tvAqiIndex.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.aqi_very_unhealthy)
                binding.aqiDetailLayout.tvAqiIndex.setTextColor(
                    ColorUtil.blackOrWhiteOf(
                        ContextCompat.getColor(context, R.color.aqi_very_unhealthy)
                    )
                )
                binding.aqiMoreLayout.aqiMoreIndex.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.aqi_very_unhealthy
                    )
                )
            }
            else -> {
                aqiDesBackgroundDrawable.setStroke(
                    resources.getDimension(R.dimen.aqi_stroke_width).toInt(),
                    ContextCompat.getColor(context, R.color.aqi_hazardous)
                )
                binding.aqiDetailLayout.aqiDesLayout.background = aqiDesBackgroundDrawable
                binding.aqiDetailLayout.tvAqiIndex.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.aqi_hazardous)
                binding.aqiDetailLayout.tvAqiIndex.setTextColor(
                    ColorUtil.blackOrWhiteOf(
                        ContextCompat.getColor(context, R.color.aqi_hazardous)
                    )
                )
                binding.aqiMoreLayout.aqiMoreIndex.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.aqi_hazardous
                    )
                )
            }
        }
    }

    private fun updateAqiDescription(aqi: Float) {
        if (PreferencesUtil.isExplicit(requireContext())) {
            binding.aqiDetailLayout.provideBy.visibility = View.VISIBLE
            when {
                aqi <= 50 -> {
                    binding.aqiDetailLayout.tvAqiLevel.text = resources.getString(R.string.aqi_good)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.aqi_good_des)
                }
                aqi <= 100 -> {
                    binding.aqiDetailLayout.tvAqiLevel.text =
                        resources.getString(R.string.aqi_moderate)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.aqi_moderate_des)
                }
                aqi <= 150 -> {
                    binding.aqiDetailLayout.tvAqiLevel.text =
                        resources.getString(R.string.aqi_unhealthy_sensitive)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.aqi_unhealthy_sensitive_des)
                }
                aqi <= 200 -> {
                    binding.aqiDetailLayout.tvAqiLevel.text =
                        resources.getString(R.string.aqi_unhealthy)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.aqi_unhealthy_des)
                }
                aqi <= 300 -> {
                    binding.aqiDetailLayout.tvAqiLevel.text =
                        resources.getString(R.string.aqi_very_unhealthy)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.aqi_very_unhealthy_des)
                }
                else -> {
                    binding.aqiDetailLayout.tvAqiLevel.text =
                        resources.getString(R.string.aqi_hazardous)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.aqi_hazardous_des)
                }
            }
        } else {
            binding.aqiDetailLayout.provideBy.visibility = View.INVISIBLE
            when {
                aqi <= 50 -> {
                    binding.aqiDetailLayout.tvAqiLevel.text = resources.getString(R.string.aqi_good)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.r_aqi_good_des)
                }
                aqi <= 100 -> {
                    binding.aqiDetailLayout.tvAqiLevel.text =
                        resources.getString(R.string.aqi_moderate)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.r_aqi_moderate_des)
                }
                aqi <= 150 -> {
                    binding.aqiDetailLayout.tvAqiLevel.text =
                        resources.getString(R.string.aqi_unhealthy_sensitive)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.r_aqi_unhealthy_sensitive_des)
                }
                aqi <= 200 -> {
                    binding.aqiDetailLayout.tvAqiLevel.text =
                        resources.getString(R.string.aqi_unhealthy)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.r_aqi_unhealthy_des)
                }
                aqi <= 300 -> {
                    binding.aqiDetailLayout.tvAqiLevel.text =
                        resources.getString(R.string.aqi_very_unhealthy)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.r_aqi_very_unhealthy_des)
                }
                else -> {
                    binding.aqiDetailLayout.tvAqiLevel.text =
                        resources.getString(R.string.aqi_hazardous)
                    binding.aqiDetailLayout.tvAqiDes.text =
                        resources.getString(R.string.r_aqi_hazardous_des)
                }
            }
        }
    }

    private fun showAqiMoreDetailsDialog() {
        aqiMoreDetailsSlideUp.show()
        aqiMoreDetailsShowing = true
    }

    fun closeAqiMoreDetailsDialog() {
        aqiMoreDetailsSlideUp.hide()
        aqiMoreDetailsShowing = false
    }
}
