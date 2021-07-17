package com.odiousPanda.rainbowKt.mainFragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.odiousPanda.rainbowKt.R
import com.odiousPanda.rainbowKt.adapters.DailyForecastAdapter
import com.odiousPanda.rainbowKt.customUI.ExpandCollapseAnimation
import com.odiousPanda.rainbowKt.customUI.MovableConstrainLayout
import com.odiousPanda.rainbowKt.databinding.FragmentHomeScreenBinding
import com.odiousPanda.rainbowKt.model.dataSource.Quote
import com.odiousPanda.rainbowKt.model.dataSource.unsplash.Unsplash
import com.odiousPanda.rainbowKt.model.dataSource.weather.Weather
import com.odiousPanda.rainbowKt.utilities.*
import com.odiousPanda.rainbowKt.viewModels.WeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*
import kotlin.math.max
import kotlin.math.min

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class HomeScreenFragment : Fragment(), QuoteGenerator.UpdateScreenQuoteListener,
    MovableConstrainLayout.OnPositionChangedCallback {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: FragmentHomeScreenBinding

    private var pointerPreviousX = 0f
    private var previousTemp = 0f
    private var previousTempColor = 0
    private var tempPreviousX = pointerPreviousX
    private var photoDetailsShowing = false
    private var quoteGenerator: QuoteGenerator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        quoteGenerator = QuoteGenerator(requireContext())
        quoteGenerator?.setUpdateScreenQuoteListener(this)
    }

    private fun setupViewModel() {
        viewModel.selectedLocationData.observe(viewLifecycleOwner, Observer {
            it.weather?.let { weather ->
                quoteGenerator?.updateHomeScreenQuote(weather)
                binding.homeLayout.isRefreshing = false
                updateTemperatureData(weather)
                updateDailyForecastData(weather)
                updateHumidityData(weather)
                updatePrecipitationData(weather)
                updateUvData(weather)
            }
            updateLocationName(it.coordinate.name)
        })

        viewModel.currentLocation.observe(viewLifecycleOwner, Observer {
            updateLocationName(it.name)
        })

        viewModel.currentTempUnit.observe(viewLifecycleOwner, Observer {
            updateTempUnit()
        })

        viewModel.isExplicit.observe(viewLifecycleOwner, Observer {
            updateExplicitSetting()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeScreenBinding.inflate(layoutInflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.layoutData.setCallback(this)

        val dailyRvLayoutManager = LinearLayoutManager(requireContext())

        binding.dailyForecastRv.apply {
            layoutManager = dailyRvLayoutManager
            addItemDecoration(
                DividerItemDecoration(
                    binding.dailyForecastRv.context,
                    dailyRvLayoutManager.orientation
                )
            )
        }

        binding.homeLayout.setOnRefreshListener {
            viewModel.refreshData()
        }

        ExpandCollapseAnimation.collapse(binding.photoDetailLayout.root)

        binding.iconInfo.setOnClickListener {
            openPhotoDetailBox()
        }

        binding.photoDetailLayout.closePhotoDetail.setOnClickListener {
            closePhotoDetailBox()
        }

        binding.photoDetailLayout.photoBy.isClickable = true
        binding.photoDetailLayout.photoBy.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun updateTempUnit() {
        viewModel.getWeather()?.let {
            it.weather?.let {  weather ->
                updateTemperatureData(weather)
                updateDailyForecastData(weather)
            }
        }
    }

    private fun updateExplicitSetting() {
        viewModel.getWeather()?.let { it.weather?.let { weather ->
            quoteGenerator?.updateHomeScreenQuote(weather)
        } }
    }

    private fun updateLocationName(locationName: String) {
        binding.tvLocation.text = locationName
    }

    fun setTextColor(textColor: Int) {
        val colorFrom = binding.tvLocation.currentTextColor
        ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, textColor).apply {
            duration = Constants.BACKGROUND_FADE_DURATION.toLong()
            addUpdateListener {
                binding.tvLocation.setTextColor(it.animatedValue as Int)
                binding.tvBigText.setTextColor(it.animatedValue as Int)
                binding.tvSmallText.setTextColor(it.animatedValue as Int)
            }
            start()
        }

        if (!PreferencesUtil.getBackgroundSetting(requireContext())
                .equals(PreferencesUtil.BACKGROUND_COLOR)
        ) {
            binding.iconInfo.visibility = View.VISIBLE
        } else {
            binding.iconInfo.visibility = View.GONE
        }

        if (textColor == Color.WHITE) {
            binding.iconInfo.setImageResource(R.drawable.ic_info_homescreen_w)
        } else {
            binding.iconInfo.setImageResource(R.drawable.ic_info_homescreen_b)
        }
    }

    private fun updateTemperatureData(weather: Weather) {
        var currentTemp = weather.currently.temperature
        val minTemp = weather.daily.data[0].temperatureMin
        val maxTemp = weather.daily.data[0].temperatureMax
        val currentTempUnit = PreferencesUtil.getTemperatureUnit(requireContext()).toString()
        currentTemp = min(maxTemp, max(currentTemp, minTemp))
        binding.tvMinTemp.text =
            UnitConverter.convertToTemperatureUnitClean(minTemp, currentTempUnit)
        binding.tvMaxTemp.text =
            UnitConverter.convertToTemperatureUnitClean(maxTemp, currentTempUnit)
        binding.tvDescription.text = TextUtil.capitalizeSentence(weather.currently.summary)
        if (previousTempColor == 0) {
            previousTempColor = ContextCompat.getColor(requireContext(), R.color.coldBlue)
        }

        try {
            binding.temperaturePointer.visibility = View.VISIBLE
            val pointerParams =
                binding.temperaturePointer.layoutParams as ConstraintLayout.LayoutParams
            val layoutWidth = binding.tempLayout.width.toFloat()
            val tempBarWidth = binding.temperatureBar.width.toFloat()
            val tvTempWidth = binding.tvTemp.width.toFloat()
            var leftMargin = (currentTemp - minTemp) / (maxTemp - minTemp) * tempBarWidth
            var tempLeftMargin = leftMargin
            if (leftMargin < tvTempWidth / 2) {
                tempLeftMargin = tvTempWidth / 2
            }
            if (layoutWidth - tempLeftMargin < tvTempWidth) {
                tempLeftMargin = layoutWidth - tvTempWidth
            }
            if (leftMargin < requireContext().resources
                    .getDimension(R.dimen.margin_4)
            ) {
                leftMargin = requireContext().resources.getDimension(R.dimen.margin_4)
            }
            if (tempBarWidth - leftMargin < requireContext()
                    .resources.getDimension(R.dimen.margin_4)
            ) {
                leftMargin -= requireContext().resources.getDimension(R.dimen.margin_4)
            }
            if (pointerPreviousX == 0f) {
                pointerPreviousX = pointerParams.leftMargin.toFloat()
            }

            val newTempColor = ColorUtil.getTemperaturePointerColor(
                requireContext(),
                (currentTemp - minTemp) / (maxTemp - minTemp)
            )

            val pointerAnimation = TranslateAnimation(pointerPreviousX, leftMargin, 0F, 0F)
            pointerAnimation.apply {
                interpolator = DecelerateInterpolator()
                duration = Constants.TEMPERATURE_ANIMATION_DURATION
                fillAfter = true
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        pointerPreviousX = leftMargin
                        tempPreviousX = tempLeftMargin
                        previousTemp = currentTemp
                        previousTempColor = newTempColor
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}

                    override fun onAnimationStart(animation: Animation?) {}

                })
            }

            binding.temperaturePointer.startAnimation(pointerAnimation)

            val tempSlideAnimation = TranslateAnimation(tempPreviousX, tempLeftMargin, 0F, 0F)
            tempSlideAnimation.apply {
                interpolator = DecelerateInterpolator()
                duration = Constants.TEMPERATURE_ANIMATION_DURATION
                fillAfter = true
            }

            binding.tvTemp.startAnimation(tempSlideAnimation)

            ValueAnimator.ofFloat(previousTemp, currentTemp).apply {
                duration = Constants.TEMPERATURE_ANIMATION_DURATION
                addUpdateListener {
                    binding.tvTemp.text = UnitConverter.convertToTemperatureUnitClean(
                        it.animatedValue as Float,
                        currentTempUnit
                    )
                }
                start()
            }

            ValueAnimator.ofObject(ArgbEvaluator(), previousTempColor, newTempColor).apply {
                duration = Constants.TEMPERATURE_ANIMATION_DURATION
                addUpdateListener {
                    val color = it.animatedValue as Int

                    requireContext().getDrawable(R.drawable.temperature_pointer)?.apply {
                        setTint(color)
                        binding.temperaturePointer.background = this
                        binding.tvTemp.setTextColor(color)
                        binding.tvDescription.setTextColor(color)
                    }
                }
                start()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateDailyForecastData(weather: Weather) {
        val adapter = DailyForecastAdapter(requireContext(), weather.daily)
        binding.dailyForecastRv.adapter = adapter
    }

    private fun updateUvData(weather: Weather) {
        val uvSummary = when {
            weather.currently.uvIndex == 0F -> {
                resources.getString(R.string.uv_summary_0)
            }
            weather.currently.uvIndex < 3 -> {
                resources.getString(R.string.uv_summary_3)
            }
            weather.currently.uvIndex < 6 -> {
                resources.getString(R.string.uv_summary_6)
            }
            weather.currently.uvIndex < 8 -> {
                resources.getString(R.string.uv_summary_8)
            }
            weather.currently.uvIndex < 11 -> {
                resources.getString(R.string.uv_summary_11)
            }
            else -> {
                resources.getString(R.string.uv_summary_11_above)
            }
        }
        binding.tvUvValue.text = weather.currently.uvIndex.toInt().toString()
        binding.tvUvSummary.text = uvSummary
    }

    private fun updateHumidityData(weather: Weather) {
        val value = "${(weather.currently.humidity * 100).toInt()}%"
        binding.tvHumidityValue.text = value
    }

    private fun updatePrecipitationData(weather: Weather) {
        val type = weather.daily.data[0].precipType
        val value = "${(weather.daily.data[0].precipProbability * 100).toInt()}%"
        binding.tvPrecipitationValue.text = value
        if (type == "rain") {
            binding.ivPrecipitationType.setImageResource(R.drawable.ic_rain_chance)
        } else {
            binding.ivPrecipitationType.setImageResource(R.drawable.ic_snow_chance)
        }

    }

    fun updatePhotoDetail(unsplash: Unsplash) {
        val photoTitle =
            if (unsplash.description != null) TextUtil.capitalizeSentence(unsplash.description.toString()) else resources.getString(
                R.string.photo_name_na
            )
        val userProfileLink = unsplash.user.links.html
        val userName = unsplash.user.name
        var cameraMaker = unsplash.exif.make
        val cameraModel =
            if (unsplash.exif.model != null) unsplash.exif.model else resources.getString(R.string.not_available)
        if (cameraMaker != null) {
            if (cameraMaker.toLowerCase(Locale.ROOT)
                    .contains(Constants.DUPLICATE_NAME_CAMERA_MAKER[0].toLowerCase(Locale.ROOT))
                || cameraMaker.toLowerCase(Locale.ROOT)
                    .contains(Constants.DUPLICATE_NAME_CAMERA_MAKER[1].toLowerCase(Locale.ROOT))
            ) {
                cameraMaker = ""
            }
        } else {
            cameraMaker = ""
        }

        val camera = "$cameraMaker $cameraModel"
        val aperture =
            if (unsplash.exif.aperture != null) "${Constants.APERTURE_PREFIX} ${unsplash.exif.aperture}" else resources.getString(
                R.string.not_available
            )
        val focalLength =
            if (unsplash.exif.focalLength != null) "${unsplash.exif.focalLength} ${Constants.FOCAL_LENGTH_SUFFIX}" else resources.getString(
                R.string.not_available
            )
        val iso =
            if (unsplash.exif.iso != null) unsplash.exif.iso.toString() else resources.getString(
                R.string.not_available
            )
        val exposureTIme =
            if (unsplash.exif.exposureTime != null) unsplash.exif.exposureTime.toString() else resources.getString(
                R.string.not_available
            )

        binding.photoDetailLayout.photoName.text = photoTitle

        binding.photoDetailLayout.photoBy.text =
            TextUtil.getReferralHtml(requireContext(), userProfileLink, userName)
        binding.photoDetailLayout.cameraName.text = camera
        binding.photoDetailLayout.tvAperture.text = aperture
        binding.photoDetailLayout.tvFocalLength.text = focalLength
        binding.photoDetailLayout.tvIso.text = iso
        binding.photoDetailLayout.tvExposureTime.text = exposureTIme
        binding.photoDetailLayout.icCamera.setImageResource(getCameraIcon(cameraMaker))
    }

    private fun getCameraIcon(maker: String): Int {
        if (maker == "") {
            return R.drawable.ic_camera
        }

        for (brand in Constants.DSLR_MAKERS) {
            if (maker.toLowerCase(Locale.ROOT).contains(brand.toLowerCase(Locale.ROOT))) {
                return R.drawable.ic_camera
            }
        }

        for (brand in Constants.DRONE_MAKERS) {
            if (maker.toLowerCase(Locale.ROOT).contains(brand.toLowerCase(Locale.ROOT))) {
                return R.drawable.ic_drone_b
            }
        }

        return if (maker.equals(Constants.APPLE_CAMERA, ignoreCase = true)) {
            R.drawable.ic_iphone_b
        } else R.drawable.ic_android_phone_b
    }

    private fun openPhotoDetailBox() {
        ExpandCollapseAnimation.expand(binding.photoDetailLayout.root)
        photoDetailsShowing = true
        binding.iconInfo.visibility = View.INVISIBLE
    }

    private fun closePhotoDetailBox() {
        ExpandCollapseAnimation.collapse(binding.photoDetailLayout.root)
        photoDetailsShowing = false
        binding.iconInfo.visibility = View.VISIBLE
    }

    private fun updateQuote(quote: Quote) {
        val text: String = quote.main
        when {
            text.length < 70 -> {
                binding.tvBigText.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.text_view_40dp)
                )
            }
            text.length < 90 -> {
                binding.tvBigText.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.text_view_36dp)
                )
            }
            else -> {
                binding.tvBigText.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.text_view_28dp)
                )
            }
        }
        binding.tvBigText.text = text
        binding.tvSmallText.text = quote.sub
    }

    override fun updateScreenQuote(randomQuote: Quote) {
        updateQuote(randomQuote)
    }

    override fun onMoved(y: Float) {
        val margin = resources.getDimension(R.dimen.margin_8)
        binding.tvSmallText.animate()
            .y(y - margin - binding.tvSmallText.height)
            .setInterpolator(DecelerateInterpolator())
            .setDuration((2 * MovableConstrainLayout.SNAP_DURATION).toLong())
            .start()
    }
}
