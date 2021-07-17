package com.odiousPanda.rainbowKt.mainFragments

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import com.odiousPanda.rainbowKt.R
import com.odiousPanda.rainbowKt.databinding.FragmentSettingBinding
import com.odiousPanda.rainbowKt.utilities.NotificationUtil
import com.odiousPanda.rainbowKt.utilities.PreferencesUtil
import com.odiousPanda.rainbowKt.utilities.TextUtil
import com.odiousPanda.rainbowKt.viewModels.WeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class SettingFragment : Fragment(), View.OnClickListener {

    private val buttonTextColor = Color.argb(255, 255, 255, 255)
    private val activeButtonTextColor = Color.argb(255, 255, 255, 255)

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: FragmentSettingBinding
    private lateinit var aboutMeSlideUp: SlideUp
    var aboutMeShowing = false
    private lateinit var notificationUtil: NotificationUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        iniViews()
        setupNotificationSetting()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.currentTempUnit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updateTempButtonColor(it)
        })

        viewModel.currentDistanceUnit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updateDistanceButtonColor(it)
        })

        viewModel.currentSpeedUnit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updateSpeedButtonColor(it)
        })

        viewModel.currentPressureUnit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updatePressureButtonColor(it)
        })

        viewModel.isExplicit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updateExplicitButtonColor(it)
        })

        viewModel.currentBackgroundSetting.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updateBackgroundSettingButtonColor(it)
        })
    }

    private fun iniViews() {
        aboutMeSlideUp = SlideUpBuilder(binding.aboutMeLayout.root)
            .withStartState(SlideUp.State.HIDDEN)
            .withStartGravity(Gravity.BOTTOM)
            .withSlideFromOtherView(binding.root)
            .withListeners(object : SlideUp.Listener.Events {
                override fun onVisibilityChanged(visibility: Int) {}

                override fun onSlide(percent: Float) {
                    aboutMeShowing = percent != 100f
                }
            })
            .build()

        binding.tvRate.setOnClickListener(this)
        binding.tvAbout.setOnClickListener(this)
        binding.aboutMeLayout.btnCloseAbout.setOnClickListener(this)
        binding.btnC.setOnClickListener(this)
        binding.btnF.setOnClickListener(this)
        binding.btnKm.setOnClickListener(this)
        binding.btnKmph.setOnClickListener(this)
        binding.btnMiph.setOnClickListener(this)
        binding.btnBananaph.setOnClickListener(this)
        binding.btnBananas.setOnClickListener(this)
        binding.btnPsi.setOnClickListener(this)
        binding.btnMmhg.setOnClickListener(this)
        binding.btnDepress.setOnClickListener(this)
        binding.btnScientist.setOnClickListener(this)
        binding.btnMi.setOnClickListener(this)
        binding.btnImNot.setOnClickListener(this)
        binding.btnHellYeah.setOnClickListener(this)
        binding.btnRandomColor.setOnClickListener(this)
        binding.btnPicture.setOnClickListener(this)
        binding.btnPictureRandom.setOnClickListener(this)
    }

    private fun setupNotificationSetting() {
        if (PreferencesUtil.getNotificationSetting(requireContext()) == PreferencesUtil.NOTIFICATION_SETTING_ON) {
            binding.dailyNotificationSwitch.isChecked = true
            binding.dailyNotificationTime.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.absoluteBlack
                )
            )
        } else {
            binding.dailyNotificationSwitch.isChecked = false
            binding.dailyNotificationTime.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.halfSnappedBlack
                )
            )
        }

        notificationUtil = NotificationUtil(requireContext())
        binding.dailyNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                PreferencesUtil.setNotificationSetting(
                    requireContext(),
                    PreferencesUtil.NOTIFICATION_SETTING_ON
                )
                binding.dailyNotificationTime.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.absoluteBlack
                    )
                )
                notificationUtil.startDailyNotification()
            } else {
                PreferencesUtil.setNotificationSetting(
                    requireContext(),
                    PreferencesUtil.NOTIFICATION_SETTING_OFF
                )
                binding.dailyNotificationTime.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.halfSnappedBlack
                    )
                )
                notificationUtil.cancelDailyNotification()
            }
        }

        val calendar = PreferencesUtil.getNotificationTime(requireContext())
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timeString =
            "${resources.getString(R.string.time_place_holder)} ${TextUtil.getTimeStringPretty(
                hour,
                minute
            )}"
        binding.dailyNotificationTime.text = timeString
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, min ->
                val time =
                    "${resources.getString(R.string.time_place_holder)} ${TextUtil.getTimeStringPretty(
                        hourOfDay,
                        min
                    )}"
                binding.dailyNotificationTime.text = time
                PreferencesUtil.setNotificationTime(requireContext(), hourOfDay, min)
                notificationUtil.cancelDailyNotification()
                notificationUtil.startDailyNotification()
            }, hour, minute, false
        )

        binding.dailyNotificationTime.setOnClickListener {
            if (PreferencesUtil.getNotificationSetting(requireContext()) == PreferencesUtil.NOTIFICATION_SETTING_ON) {
                timePickerDialog.show()
            }
        }
    }

    private fun changeTempUnit(id: Int) {
        when (id) {
            R.id.btn_c -> {
                viewModel.updateTempUnit(resources.getString(R.string.temp_setting_degree_c))
            }
            R.id.btn_f -> {
                viewModel.updateTempUnit(resources.getString(R.string.temp_setting_degree_f))
            }
            else -> {
                viewModel.updateTempUnit(resources.getString(R.string.temp_setting_degree_k))
            }
        }
    }

    private fun updateTempButtonColor(unit: String) {
        when (unit) {
            resources.getString(R.string.temp_setting_degree_c) -> {
                binding.btnC.setBackgroundResource(R.drawable.left_button_active)
                binding.btnF.setBackgroundResource(R.drawable.middle_button)
                binding.btnScientist.setBackgroundResource(R.drawable.right_button)
                binding.btnC.setTextColor(activeButtonTextColor)
                binding.btnF.setTextColor(buttonTextColor)
                binding.btnScientist.setTextColor(buttonTextColor)
            }
            resources.getString(R.string.temp_setting_degree_f) -> {
                binding.btnC.setBackgroundResource(R.drawable.left_button)
                binding.btnF.setBackgroundResource(R.drawable.middle_button_active)
                binding.btnScientist.setBackgroundResource(R.drawable.right_button)
                binding.btnC.setTextColor(buttonTextColor)
                binding.btnF.setTextColor(activeButtonTextColor)
                binding.btnScientist.setTextColor(buttonTextColor)
            }
            else -> {
                binding.btnC.setBackgroundResource(R.drawable.left_button)
                binding.btnF.setBackgroundResource(R.drawable.middle_button)
                binding.btnScientist.setBackgroundResource(R.drawable.right_button_active)
                binding.btnC.setTextColor(buttonTextColor)
                binding.btnF.setTextColor(buttonTextColor)
                binding.btnScientist.setTextColor(activeButtonTextColor)
            }
        }
    }

    private fun changeDistanceUnit(id: Int) {
        when (id) {
            R.id.btn_km -> {
                viewModel.updateDistanceUnit(resources.getString(R.string.km))
            }
            R.id.btn_mi -> {
                viewModel.updateDistanceUnit(resources.getString(R.string.mi_uni))
            }
            else -> {
                viewModel.updateDistanceUnit(resources.getString(R.string.bananas_unit))
            }
        }
    }

    private fun updateDistanceButtonColor(unit: String) {
        when (unit) {
            resources.getString(R.string.km) -> {
                binding.btnKm.setBackgroundResource(R.drawable.left_button_active)
                binding.btnMi.setBackgroundResource(R.drawable.middle_button)
                binding.btnBananas.setBackgroundResource(R.drawable.right_button)
                binding.btnKm.setTextColor(activeButtonTextColor)
                binding.btnMi.setTextColor(buttonTextColor)
                binding.btnBananas.setTextColor(buttonTextColor)
            }
            resources.getString(R.string.mi_uni) -> {
                binding.btnKm.setBackgroundResource(R.drawable.left_button)
                binding.btnMi.setBackgroundResource(R.drawable.middle_button_active)
                binding.btnBananas.setBackgroundResource(R.drawable.right_button)
                binding.btnKm.setTextColor(buttonTextColor)
                binding.btnMi.setTextColor(activeButtonTextColor)
                binding.btnBananas.setTextColor(buttonTextColor)
            }
            else -> {
                binding.btnKm.setBackgroundResource(R.drawable.left_button)
                binding.btnMi.setBackgroundResource(R.drawable.middle_button)
                binding.btnBananas.setBackgroundResource(R.drawable.right_button_active)
                binding.btnKm.setTextColor(buttonTextColor)
                binding.btnMi.setTextColor(buttonTextColor)
                binding.btnBananas.setTextColor(activeButtonTextColor)
            }
        }
    }

    private fun changeSpeedUnit(id: Int) {
        when (id) {
            R.id.btn_kmph -> {
                viewModel.updateSpeedUnit(resources.getString(R.string.kmph))
            }
            R.id.btn_miph -> {
                viewModel.updateSpeedUnit(resources.getString(R.string.miph))
            }
            else -> {
                viewModel.updateSpeedUnit(resources.getString(R.string.bananas_h_unit))
            }
        }
    }

    private fun updateSpeedButtonColor(unit: String) {
        when (unit) {
            resources.getString(R.string.kmph) -> {
                binding.btnKmph.setBackgroundResource(R.drawable.left_button_active)
                binding.btnMiph.setBackgroundResource(R.drawable.middle_button)
                binding.btnBananaph.setBackgroundResource(R.drawable.right_button)
                binding.btnKmph.setTextColor(activeButtonTextColor)
                binding.btnMiph.setTextColor(buttonTextColor)
                binding.btnBananaph.setTextColor(buttonTextColor)
            }
            resources.getString(R.string.miph) -> {
                binding.btnKmph.setBackgroundResource(R.drawable.left_button)
                binding.btnMiph.setBackgroundResource(R.drawable.middle_button_active)
                binding.btnBananaph.setBackgroundResource(R.drawable.right_button)
                binding.btnKmph.setTextColor(buttonTextColor)
                binding.btnMiph.setTextColor(activeButtonTextColor)
                binding.btnBananaph.setTextColor(buttonTextColor)
            }
            else -> {
                binding.btnKmph.setBackgroundResource(R.drawable.left_button)
                binding.btnMiph.setBackgroundResource(R.drawable.middle_button)
                binding.btnBananaph.setBackgroundResource(R.drawable.right_button_active)
                binding.btnKmph.setTextColor(buttonTextColor)
                binding.btnMiph.setTextColor(buttonTextColor)
                binding.btnBananaph.setTextColor(activeButtonTextColor)
            }
        }
    }

    private fun changePressureUnit(id: Int) {
        when (id) {
            R.id.btn_psi -> {
                viewModel.updatePressureUnit(resources.getString(R.string.psi))
            }
            R.id.btn_mmhg -> {
                viewModel.updatePressureUnit(resources.getString(R.string.mmhg))
            }
            else -> {
                viewModel.updatePressureUnit(resources.getString(R.string.depression_unit))
            }
        }
    }

    private fun updatePressureButtonColor(unit: String) {
        when (unit) {
            resources.getString(R.string.psi) -> {
                binding.btnPsi.setBackgroundResource(R.drawable.left_button_active)
                binding.btnMmhg.setBackgroundResource(R.drawable.middle_button)
                binding.btnDepress.setBackgroundResource(R.drawable.right_button)
                binding.btnPsi.setTextColor(activeButtonTextColor)
                binding.btnMmhg.setTextColor(buttonTextColor)
                binding.btnDepress.setTextColor(buttonTextColor)
            }
            resources.getString(R.string.mmhg) -> {
                binding.btnPsi.setBackgroundResource(R.drawable.left_button)
                binding.btnMmhg.setBackgroundResource(R.drawable.middle_button_active)
                binding.btnDepress.setBackgroundResource(R.drawable.right_button)
                binding.btnPsi.setTextColor(buttonTextColor)
                binding.btnMmhg.setTextColor(activeButtonTextColor)
                binding.btnDepress.setTextColor(buttonTextColor)
            }
            else -> {
                binding.btnPsi.setBackgroundResource(R.drawable.left_button)
                binding.btnMmhg.setBackgroundResource(R.drawable.middle_button)
                binding.btnDepress.setBackgroundResource(R.drawable.right_button_active)
                binding.btnPsi.setTextColor(buttonTextColor)
                binding.btnMmhg.setTextColor(buttonTextColor)
                binding.btnDepress.setTextColor(activeButtonTextColor)
            }
        }
    }

    private fun changeBackgroundSetting(id: Int) {
        when (id) {
            R.id.btn_random_color -> {
                viewModel.updateBackgroundSetting(PreferencesUtil.BACKGROUND_COLOR)
            }
            R.id.btn_picture -> {
                viewModel.updateBackgroundSetting(PreferencesUtil.BACKGROUND_PICTURE)
            }
            R.id.btn_picture_random -> {
                viewModel.updateBackgroundSetting(PreferencesUtil.BACKGROUND_PICTURE_RANDOM)
            }
        }
    }

    private fun updateBackgroundSettingButtonColor(setting: String) {
        when (setting) {
            PreferencesUtil.BACKGROUND_COLOR -> {
                binding.btnRandomColor.setBackgroundResource(R.drawable.left_button_active)
                binding.btnPicture.setBackgroundResource(R.drawable.middle_button)
                binding.btnPictureRandom.setBackgroundResource(R.drawable.right_button)
                binding.btnRandomColor.setTextColor(activeButtonTextColor)
                binding.btnPicture.setTextColor(buttonTextColor)
                binding.btnPictureRandom.setTextColor(buttonTextColor)
            }
            PreferencesUtil.BACKGROUND_PICTURE -> {
                binding.btnRandomColor.setBackgroundResource(R.drawable.left_button)
                binding.btnPicture.setBackgroundResource(R.drawable.middle_button_active)
                binding.btnPictureRandom.setBackgroundResource(R.drawable.right_button)
                binding.btnRandomColor.setTextColor(buttonTextColor)
                binding.btnPicture.setTextColor(activeButtonTextColor)
                binding.btnPictureRandom.setTextColor(buttonTextColor)
            }
            else -> {
                binding.btnRandomColor.setBackgroundResource(R.drawable.left_button)
                binding.btnPicture.setBackgroundResource(R.drawable.middle_button)
                binding.btnPictureRandom.setBackgroundResource(R.drawable.right_button_active)
                binding.btnRandomColor.setTextColor(buttonTextColor)
                binding.btnPicture.setTextColor(buttonTextColor)
                binding.btnPictureRandom.setTextColor(activeButtonTextColor)
            }
        }
    }

    private fun changeExplicitSetting(id: Int) {
        if (id == R.id.btn_im_not) {
            viewModel.updateExplicitSetting(true)
        } else {
            viewModel.updateExplicitSetting(false)
        }
    }

    private fun updateExplicitButtonColor(isExplicit: Boolean) {
        if (isExplicit) {
            binding.btnImNot.setBackgroundResource(R.drawable.left_button_active)
            binding.btnHellYeah.setBackgroundResource(R.drawable.right_button)
            binding.btnImNot.setTextColor(activeButtonTextColor)
            binding.btnHellYeah.setTextColor(buttonTextColor)
        } else {
            binding.btnImNot.setBackgroundResource(R.drawable.left_button)
            binding.btnHellYeah.setBackgroundResource(R.drawable.right_button_active)
            binding.btnImNot.setTextColor(buttonTextColor)
            binding.btnHellYeah.setTextColor(activeButtonTextColor)
        }
    }

    override fun onClick(v: View) {
        when (val id = v.id) {
            R.id.btn_c, R.id.btn_f, R.id.btn_scientist -> {
                changeTempUnit(id)
            }
            R.id.btn_km, R.id.btn_mi, R.id.btn_bananas -> {
                changeDistanceUnit(id)
            }
            R.id.btn_kmph, R.id.btn_miph, R.id.btn_bananaph -> {
                changeSpeedUnit(id)
            }
            R.id.btn_psi, R.id.btn_mmhg, R.id.btn_depress -> {
                changePressureUnit(id)
            }
            R.id.btn_im_not, R.id.btn_hell_yeah -> {
                changeExplicitSetting(id)
            }
            R.id.tv_rate -> {
                rateThisApp()
            }
            R.id.tv_about -> {
                showAboutMeDialog()
            }
            R.id.btn_close_about -> {
                closeAboutMeDialog()
            }
            R.id.btn_random_color, R.id.btn_picture, R.id.btn_picture_random -> {
                changeBackgroundSetting(id)
            }
            else -> {
            }
        }
    }

    fun closeAboutMeDialog() {
        aboutMeSlideUp.hide()
        aboutMeShowing = false
    }

    private fun showAboutMeDialog() {
        aboutMeSlideUp.show()
        aboutMeShowing = true
    }

    private fun rateThisApp() {
        Snackbar.make(binding.tvRate, getString(R.string.feature_not_ready), Snackbar.LENGTH_SHORT).show()
    }
}
