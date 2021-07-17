package com.odiousPanda.rainbowKt.activities

import android.Manifest
import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.FirebaseApp
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import com.odiousPanda.rainbowKt.R
import com.odiousPanda.rainbowKt.adapters.LocationListAdapter
import com.odiousPanda.rainbowKt.adapters.SectionsPagerAdapter
import com.odiousPanda.rainbowKt.adapters.SwipeToDeleteCallback
import com.odiousPanda.rainbowKt.apis.APIConstants
import com.odiousPanda.rainbowKt.apis.Result
import com.odiousPanda.rainbowKt.apis.RetrofitService
import com.odiousPanda.rainbowKt.databinding.ActivityMainBinding
import com.odiousPanda.rainbowKt.mainFragments.DetailsFragment
import com.odiousPanda.rainbowKt.mainFragments.HomeScreenFragment
import com.odiousPanda.rainbowKt.mainFragments.SettingFragment
import com.odiousPanda.rainbowKt.model.dataSource.Coordinate
import com.odiousPanda.rainbowKt.utilities.ColorUtil
import com.odiousPanda.rainbowKt.utilities.Constants
import com.odiousPanda.rainbowKt.utilities.PreferencesUtil
import com.odiousPanda.rainbowKt.utilities.TextUtil
import com.odiousPanda.rainbowKt.viewModels.BackgroundViewModel
import com.odiousPanda.rainbowKt.viewModels.WeatherViewModel
import kotlinx.coroutines.*
import java.util.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainActivity : AppCompatActivity(), LocationListAdapter.OnLocationListAdapterActionListener {

    companion object {
        const val REQUEST_CODE_LOCATION_PERMISSION = 11
        private const val AUTOCOMPLETE_REQUEST_CODE = 1201
        private const val TAG = "weatherA"
    }

    private val connectionChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (isConnected(context)) {
                hideNoConnection()
            } else {
                showNoConnection()
            }
        }
    }

    private lateinit var viewModel: WeatherViewModel

    private lateinit var binding: ActivityMainBinding

    private val homeScreenFragment = HomeScreenFragment()
    private val settingFragment = SettingFragment()
    private val detailsFragment = DetailsFragment()

    private var oldBackgroundDrawable: Drawable? = null
    private var slideUp: SlideUp? = null
    private var firstTimeLoading = true
    private var locationListShowing = false
    private lateinit var locationListAdapter: LocationListAdapter

    private val backgroundViewModel: BackgroundViewModel by viewModels()

    private var currentBackgroundColor = Color.argb(255, 255, 255, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PreferencesUtil.increaseAppOpenCount(this)
        Places.initialize(applicationContext, APIConstants.GOOGLE_API_KEY)
        initViews()
        checkPermissions()
        FirebaseApp.initializeApp(this)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        viewModel.currentLocation.observe(this, Observer {
            viewModel.getCurrentLocationWeatherAndAQI()
        })

        viewModel.currentLocationWeather.observe(this, Observer {
            when (it) {
                Result.Loading -> {
                    if(firstTimeLoading) {
                        showLoading()
                    }
                }
                is Result.Success -> {
                    locationListAdapter.updateCurrentLocationData(it.data)
                    viewModel.selectLocation(0)
                    hideLoading()
                }
                Result.Error -> {
                    hideLoading()
                }
            }
        })

        viewModel.selectedLocationData.observe(this, Observer {
            updateBackground()
        })

        viewModel.currentWeatherList.observe(this, Observer { list ->
            locationListAdapter.updateUserAddedLocationData(list)
            binding.pbLocationList.visibility = View.INVISIBLE
        })

        viewModel.savedCoordinates.observe(this, Observer {
            viewModel.getWeatherList()
        })

        viewModel.currentTempUnit.observe(this, Observer {
            viewModel.currentWeatherList.value?.let { list ->
                locationListAdapter.updateUserAddedLocationData(list)
            }
        })

        viewModel.currentBackgroundSetting.observe(this, Observer {
            if (homeScreenFragment.isAdded) {
                updateBackground()
            }
        })

        backgroundViewModel.currentImageBackgroundInfo.observe(this, Observer {
            when (it) {
                is Result.Success -> {
                    Glide.with(this).load(it.data.urls.regular)
                        .placeholder(oldBackgroundDrawable)
                        .transition(DrawableTransitionOptions.withCrossFade(Constants.BACKGROUND_FADE_DURATION))
                        .centerCrop()
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                val bitmap = (resource as BitmapDrawable).bitmap
                                val p = Palette.from(bitmap).generate()
                                val backgroundColor =
                                    p.getDominantColor(Color.BLACK)
                                val textColor = ColorUtil.blackOrWhiteOf(backgroundColor)
                                homeScreenFragment.setTextColor(textColor)
                                oldBackgroundDrawable = resource
                                return false
                            }
                        })
                        .into(binding.background)
                    homeScreenFragment.updatePhotoDetail(it.data)
                }
            }
        })
    }

    private fun initViews() {
        showLoading()
        binding.loadingIcon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spin))

        oldBackgroundDrawable = getDrawable(R.drawable.background_placeholder)
        val sectionsPagerAdapter =
            SectionsPagerAdapter(supportFragmentManager, listOf(settingFragment, homeScreenFragment, detailsFragment))
        binding.contentContainer.apply {
            adapter = sectionsPagerAdapter
            offscreenPageLimit = 3
            currentItem = 1
            addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {}

                override fun onPageSelected(position: Int) {
                    if(position == 1 && !locationListShowing){
                        binding.fab.show()
                        settingFragment.closeAboutMeDialog()
                        detailsFragment.closeAqiMoreDetailsDialog()
                    } else {
                        binding.fab.hide()
                    }
                }
            })
        }

        slideUp = SlideUpBuilder(binding.locationListLayout)
            .withStartState(SlideUp.State.HIDDEN)
            .withStartGravity(Gravity.BOTTOM)
            .withSlideFromOtherView(binding.rootLayout)
            .withListeners(object : SlideUp.Listener.Events {
                override fun onVisibilityChanged(visibility: Int) {}

                override fun onSlide(percent: Float) {
                    if (percent == 100f && binding.loadingLayout.visibility == View.INVISIBLE && binding.noConnectionLayout.visibility == View.INVISIBLE) {
                        binding.fab.show()
                        locationListShowing = false
                    } else if (percent < 100) {
                        locationListShowing = true
                        binding.fab.hide()
                    } else {
                        binding.fab.hide()
                    }
                }
            })
            .build()

        binding.fab.setOnClickListener { showLocationList() }
        binding.btnGoBack.setOnClickListener { hideLocationList() }
        binding.btnAddLocation.setOnClickListener { openLocationSearch() }

        locationListAdapter = LocationListAdapter(this, this)
        binding.locationsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationListAdapter
            setHasFixedSize(true)
            ItemTouchHelper(SwipeToDeleteCallback(locationListAdapter)).attachToRecyclerView(this)
        }
    }

    private fun updateBackground() {
        when (PreferencesUtil.getBackgroundSetting(this)) {
            PreferencesUtil.BACKGROUND_PICTURE -> updateBackgroundWeatherPicture()
            PreferencesUtil.BACKGROUND_COLOR -> updateBackgroundColor()
            PreferencesUtil.BACKGROUND_PICTURE_RANDOM -> updateBackgroundRandomPicture()
        }
    }

    private fun updateBackgroundColor() {
        binding.background.setImageResource(android.R.color.transparent)
        val argb = ColorUtil.randomColorCode()
        val textColorCode = ColorUtil.blackOrWhiteOf(argb)
        ObjectAnimator.ofObject(
            binding.background,
            "backgroundColor",
            ArgbEvaluator(),
            currentBackgroundColor,
            Color.argb(argb[0], argb[1], argb[2], argb[3])
        ).apply {
            duration = Constants.BACKGROUND_FADE_DURATION.toLong()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    currentBackgroundColor = Color.argb(argb[0], argb[1], argb[2], argb[3])
                    oldBackgroundDrawable = ColorDrawable(currentBackgroundColor)
                }

                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

            })
            start()
        }
        homeScreenFragment.setTextColor(textColorCode)
    }

    private fun updateBackgroundRandomPicture() {
        backgroundViewModel.getRandomBackground()
    }

    private fun updateBackgroundWeatherPicture() {
        var query = ""
        viewModel.getWeather()?.let {
            val iconRaw = it.weather?.currently?.icon.toString()
            query = iconRaw.replace("-", " ")
        }
        backgroundViewModel.getWeatherBackground(query)
    }

    private fun searchNearbyPlaceToEat(lat: String, lon: String){
        val locationString = TextUtil.locationStringForNearbySearch(lat, lon)
        val radius = APIConstants.NEARBY_SEARCH_RADIUS_DEFAULT
        val keyword = APIConstants.NEARBY_SEARCH_KEYWORD
        val apiKey = APIConstants.GOOGLE_API_KEY

        CoroutineScope(Dispatchers.IO).launch {
            val response =  RetrofitService.createNearbySearchCall().searchNearby(locationString, radius, keyword, apiKey)
            if(response.isSuccessful) {
                val nearbySearch = response.body()!!
                withContext(Dispatchers.Main) {
                    detailsFragment.updateNearbySearchData(nearbySearch)
                }
            }
        }
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    private fun showNoConnection() {
        binding.noConnectionLayout.visibility = View.VISIBLE
        binding.fab.hide()
    }

    private fun hideNoConnection() {
        binding.noConnectionLayout.visibility = View.INVISIBLE
        if(!locationListShowing && binding.contentContainer.currentItem == 1) {
            binding.fab.show()
        }
    }

    private fun showLoading() {
        binding.loadingLayout.visibility = View.VISIBLE
        binding.fab.hide()
    }

    private fun hideLoading() {
        firstTimeLoading = false
        binding.loadingLayout.visibility = View.INVISIBLE
        if(!locationListShowing) {
            binding.fab.show()
        }
    }

    private fun hideLocationList() {
        slideUp?.hide()
        if (binding.contentContainer.currentItem == 1) {
            binding.fab.show()
        }
        locationListShowing = false
    }

    private fun showLocationList() {
        slideUp?.show()
        binding.fab.hide()
        locationListShowing = true
    }

    private fun openLocationSearch() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setTypeFilter(TypeFilter.REGIONS)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    /*
     * Check if location permission is allowed
     * by the user, if not, request permission
     */
    private fun checkPermissions() {
        val checkPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            PackageManager.PERMISSION_GRANTED
        }

        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), REQUEST_CODE_LOCATION_PERMISSION
                )
            }
        } else {
            updateCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateCurrentLocation() {
        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
            val oldCurrentCoordinate = PreferencesUtil.getCurrentLocation(this)
            var currentLocation = Coordinate(oldCurrentCoordinate.lat, oldCurrentCoordinate.lon)
            if (it != null) {
                currentLocation = Coordinate(it.latitude.toString(), it.longitude.toString())
            } else {
                Log.d(TAG, "FusedLocationProvider return null")
            }
            currentLocation.name = geoCodeLocationName(currentLocation)
            searchNearbyPlaceToEat(currentLocation.lat, currentLocation.lon)
            Log.d(TAG, "updateCurrentLocation: ${currentLocation.lat}/${currentLocation.lon} ")
            viewModel.updateCurrentLocation(currentLocation)
        }
    }

    private fun geoCodeLocationName(currentLocation: Coordinate): String{
        try {
            val geo = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geo.getFromLocation(
                currentLocation.lat.toDouble(),
                currentLocation.lon.toDouble(),
                1
            )
            return if (addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                val addressPieces = address.split(",").toTypedArray()
                val locationName = if (addressPieces.size >= 3) {
                    addressPieces[addressPieces.size - 3].trim()
                } else {
                    addressPieces[addressPieces.size - 2].trim()
                }
                locationName
            } else {
                resources.getString(R.string.currentLocation)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception in getting location name: ${e.message}")
            return resources.getString(R.string.currentLocation)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //All permissions are allowed
                updateCurrentLocation()
            } else {
                //One or more of the permissions not allowed
                checkPermissions()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume, $locationListShowing, ${binding.contentContainer.currentItem}")
        if (oldBackgroundDrawable != null) {
            binding.background.setImageDrawable(oldBackgroundDrawable)
        }
        registerReceiver(
            connectionChangeReceiver,
            IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        )
        binding.fab.hide()
        if(!locationListShowing && binding.contentContainer.currentItem == 1) {
            binding.fab.show()
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        unregisterReceiver(connectionChangeReceiver)
        super.onPause()
    }

    override fun onItemClick(position: Int) {
        viewModel.selectLocation(position)
        hideLocationList()
    }

    override fun onDeleteItem(position: Int) {
        viewModel.deleteLocation(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            if(resultCode == RESULT_OK){
                val place = Autocomplete.getPlaceFromIntent(data!!)
                place.latLng?.let {
                    val coordinate = Coordinate().apply {
                        lat = it.latitude.toString()
                        lon = it.longitude.toString()
                        name = place.name.toString()
                    }
                    binding.pbLocationList.visibility = View.VISIBLE
                    viewModel.insertCoordinate(coordinate)
                }
            }else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.d(TAG, "${status.statusMessage}")
            }
        }
    }
}
