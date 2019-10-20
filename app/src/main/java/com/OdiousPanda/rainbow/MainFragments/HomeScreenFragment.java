package com.OdiousPanda.rainbow.MainFragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.OdiousPanda.rainbow.Adapters.DailyForecastAdapter;
import com.OdiousPanda.rainbow.CustomUI.ExpandCollapseAnimation;
import com.OdiousPanda.rainbow.CustomUI.MovableConstrainLayout;
import com.OdiousPanda.rainbow.DataModel.Quote;
import com.OdiousPanda.rainbow.DataModel.Unsplash.Unsplash;
import com.OdiousPanda.rainbow.DataModel.Weather.Weather;
import com.OdiousPanda.rainbow.R;
import com.OdiousPanda.rainbow.Utilities.ColorUtil;
import com.OdiousPanda.rainbow.Utilities.Constants;
import com.OdiousPanda.rainbow.Utilities.PreferencesUtil;
import com.OdiousPanda.rainbow.Utilities.QuoteGenerator;
import com.OdiousPanda.rainbow.Utilities.TextUtil;
import com.OdiousPanda.rainbow.Utilities.UnitConverter;

import java.util.Objects;

public class HomeScreenFragment extends Fragment implements MovableConstrainLayout.OnPositionChangedCallback, QuoteGenerator.UpdateScreenQuoteListener {

    public boolean photoDetailsShowing = false;
    private MovableConstrainLayout layoutData;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvTemp;
    private TextView tvDescription;
    private TextView tvBigText;
    private TextView tvSmallText;
    private ImageView iconInfo;
    private TextView tvLocation;
    private View tempBar;
    private View tempPointer;
    private TextView tvMinTemp;
    private TextView tvMaxTemp;
    private TextView tvUvIndex;
    private TextView tvHumidity;
    private TextView tvPrecipitation;
    private ImageView icPrecipitationType;
    private TextView tvUvSummary;
    private ConstraintLayout tempLayout;
    private RecyclerView rvDailyForecast;
    private ConstraintLayout photoDetailsLayout;
    private ImageView btnClosePhotoDetails;
    private TextView tvPhotoTitle;
    private TextView tvPhotoBy;
    private TextView tvCamera;
    private ImageView icCamera;
    private TextView tvAperture;
    private TextView tvExposureTime;
    private TextView tvIso;
    private TextView tvFocalLength;
    private ImageView btnShare;
    private QuoteGenerator quoteGenerator;
    private Weather currentWeather;
    private float pointerPreviousX = 0;
    private float previousTemp = 0;
    private int previousTempColor = 0;
    private float tempPreviousX = pointerPreviousX;
    private OnLayoutRefreshListener callback;

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_screen, container, false);
        initViews(v);
        quoteGenerator = new QuoteGenerator(getActivity());
        quoteGenerator.setUpdateScreenQuoteListener(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initViews(View v) {
        layoutData = v.findViewById(R.id.layout_data);
        layoutData.setCallback(this);
        tvBigText = v.findViewById(R.id.big_text);
        tvDescription = v.findViewById(R.id.tv_description);
        tvSmallText = v.findViewById(R.id.small_text);
        tvTemp = v.findViewById(R.id.tv_temp);
        iconInfo = v.findViewById(R.id.icon);
        btnShare = v.findViewById(R.id.shareButton);
        tvLocation = v.findViewById(R.id.tv_location);
        tempBar = v.findViewById(R.id.temperature_bar);
        tempPointer = v.findViewById(R.id.temperature_pointer);
        tvMinTemp = v.findViewById(R.id.min_temp);
        tvMaxTemp = v.findViewById(R.id.max_temp);
        tvUvIndex = v.findViewById(R.id.uv_value);
        tvHumidity = v.findViewById(R.id.humidity_value);
        tvPrecipitation = v.findViewById(R.id.precipitation_value);
        icPrecipitationType = v.findViewById(R.id.precipitation_type);
        tvUvSummary = v.findViewById(R.id.uv_summary);
        tempLayout = v.findViewById(R.id.temp_layout);
        if (PreferencesUtil.getBackgroundSetting(Objects.requireNonNull(getActivity())).equals(PreferencesUtil.BACKGROUND_PICTURE_RANDOM)) {
            iconInfo.setVisibility(View.VISIBLE);
        } else {
            iconInfo.setVisibility(View.GONE);
        }
        photoDetailsLayout = v.findViewById(R.id.photo_detail_layout);
        tvPhotoTitle = v.findViewById(R.id.photo_name);
        tvPhotoBy = v.findViewById(R.id.photo_by);
        btnClosePhotoDetails = v.findViewById(R.id.close_photo_detail);
        tvCamera = v.findViewById(R.id.camera_name);
        icCamera = v.findViewById(R.id.ic_camera);
        tvAperture = v.findViewById(R.id.tvAperture);
        tvExposureTime = v.findViewById(R.id.tvExposureTime);
        tvFocalLength = v.findViewById(R.id.tvFocalLength);
        tvIso = v.findViewById(R.id.tvIso);
        tvPhotoBy.setClickable(true);
        tvPhotoBy.setMovementMethod(LinkMovementMethod.getInstance());
        ExpandCollapseAnimation.collapse(photoDetailsLayout);
        iconInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotoDetailBox();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeScreenShotIntent = new Intent(Constants.ACTION_SHARE_RAINBOW);
                Objects.requireNonNull(getActivity()).sendBroadcast(takeScreenShotIntent);
            }
        });
        btnClosePhotoDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePhotoDetailBox();
            }
        });

        rvDailyForecast = v.findViewById(R.id.daily_forecast_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvDailyForecast.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvDailyForecast.getContext(),
                linearLayoutManager.getOrientation());
        rvDailyForecast.addItemDecoration(dividerItemDecoration);
        swipeRefreshLayout = v.findViewById(R.id.home_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callback.updateData();
            }
        });
    }

    public void setColorTheme(int textColor) {
        int colorFrom = tvTemp.getCurrentTextColor();
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, textColor);
        colorAnimation.setDuration(Constants.BACKGROUND_FADE_DURATION);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                tvLocation.setTextColor((int) animator.getAnimatedValue());
                tvBigText.setTextColor((int) animator.getAnimatedValue());
                tvSmallText.setTextColor((int) animator.getAnimatedValue());
            }

        });
        ConstraintLayout.LayoutParams shareParam = (ConstraintLayout.LayoutParams) btnShare.getLayoutParams();
        if (!PreferencesUtil.getBackgroundSetting(Objects.requireNonNull(getActivity())).equals(PreferencesUtil.BACKGROUND_COLOR)) {
            iconInfo.setVisibility(View.VISIBLE);
            shareParam.setMarginEnd((int) getResources().getDimension(R.dimen.margin_16));
            btnShare.setLayoutParams(shareParam);
        } else {
            iconInfo.setVisibility(View.GONE);
            shareParam.setMarginEnd(0);
            btnShare.setLayoutParams(shareParam);
        }
        colorAnimation.start();
        if (textColor == Color.WHITE) {
            iconInfo.setImageResource(R.drawable.ic_info_w);
            btnShare.setImageResource(R.drawable.ic_share_w);
        } else {
            iconInfo.setImageResource(R.drawable.ic_info_b);
            btnShare.setImageResource(R.drawable.ic_share_b);
        }
    }

    public void updateUnit() {
        String currentTempUnit = PreferencesUtil.getTemperatureUnit(Objects.requireNonNull(getActivity()));
        tvTemp.setText(UnitConverter.convertToTemperatureUnitClean(currentWeather.getCurrently().getTemperature(), currentTempUnit));
        tvMinTemp.setText(UnitConverter.convertToTemperatureUnitClean(currentWeather.getDaily().getData().get(0).getTemperatureLow(), currentTempUnit));
        tvMaxTemp.setText(UnitConverter.convertToTemperatureUnitClean(currentWeather.getDaily().getData().get(0).getTemperatureMax(), currentTempUnit));
        updatePrecipitationData();
        DailyForecastAdapter adapter = new DailyForecastAdapter(getActivity(), currentWeather.getDaily());
        rvDailyForecast.setAdapter(adapter);
    }

    public void updateData(Weather weather) {
        currentWeather = weather;
        quoteGenerator.updateHomeScreenQuote(weather);
        updateUvData();
        updateTemperatureData();
        updateHumidityData();
        updatePrecipitationData();
        updateDailyForecastData();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void updateTemperatureData() {
        float currentTemp = currentWeather.getCurrently().getTemperature();
        final float minTemp = currentWeather.getDaily().getData().get(0).getTemperatureLow();
        final float maxTemp = currentWeather.getDaily().getData().get(0).getTemperatureMax();
        final String currentTempUnit = PreferencesUtil.getTemperatureUnit(Objects.requireNonNull(getActivity()));
        currentTemp = Math.min(maxTemp, Math.max(currentTemp, minTemp));
        tvMinTemp.setText(UnitConverter.convertToTemperatureUnitClean(minTemp, currentTempUnit));
        tvMaxTemp.setText(UnitConverter.convertToTemperatureUnitClean(maxTemp, currentTempUnit));
        tvDescription.setText(currentWeather.getCurrently().getSummary());
        if (previousTempColor == 0) {
            previousTempColor = ContextCompat.getColor(getActivity(), R.color.coldBlue);
        }
        final float finalCurrentTemp = currentTemp;
        tempBar.post(new Runnable() {
            @Override
            public void run() {
                try {
                    tempPointer.setVisibility(View.VISIBLE);
                    ConstraintLayout.LayoutParams pointerParams = (ConstraintLayout.LayoutParams) tempPointer.getLayoutParams();
                    float layoutWidth = (float) tempLayout.getWidth();
                    float tempBarWidth = (float) tempBar.getWidth();
                    float tvTempWidth = (float) tvTemp.getWidth();
                    float leftMargin = (finalCurrentTemp - minTemp) / (maxTemp - minTemp) * tempBarWidth;
                    float tempLeftMargin = leftMargin;
                    if (leftMargin < tvTempWidth / 2) {
                        tempLeftMargin = tvTempWidth / 2;
                    }
                    if (layoutWidth - tempLeftMargin < tvTempWidth) {
                        tempLeftMargin = layoutWidth - tvTempWidth;
                    }
                    if (leftMargin < Objects.requireNonNull(getActivity()).getResources().getDimension(R.dimen.margin_4)) {
                        leftMargin = getActivity().getResources().getDimension(R.dimen.margin_4);
                    }
                    if (tempBarWidth - leftMargin < Objects.requireNonNull(getActivity()).getResources().getDimension(R.dimen.margin_4)) {
                        leftMargin -= getActivity().getResources().getDimension(R.dimen.margin_4);
                    }
                    if (pointerPreviousX == 0) {
                        pointerPreviousX = pointerParams.leftMargin;
                    }
                    final int newTempColor = ColorUtil.getTemperaturePointerColor(Objects.requireNonNull(getActivity()), (finalCurrentTemp - minTemp) / (maxTemp - minTemp));
                    Animation pointerSlideAnimation = new TranslateAnimation(pointerPreviousX, leftMargin, 0, 0);
                    pointerSlideAnimation.setInterpolator(new DecelerateInterpolator());
                    pointerSlideAnimation.setDuration(Constants.TEMPERATURE_ANIMATION_DURATION);
                    pointerSlideAnimation.setFillAfter(true);
                    final float finalLeftMargin = leftMargin;
                    final float finalTempLeftMargin = tempLeftMargin;
                    pointerSlideAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            pointerPreviousX = finalLeftMargin;
                            tempPreviousX = finalTempLeftMargin;
                            previousTemp = finalCurrentTemp;
                            previousTempColor = newTempColor;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    Animation tempSlideAnimation = new TranslateAnimation(tempPreviousX, tempLeftMargin, 0, 0);
                    tempSlideAnimation.setInterpolator(new DecelerateInterpolator());
                    tempSlideAnimation.setDuration(Constants.TEMPERATURE_ANIMATION_DURATION);
                    tempSlideAnimation.setFillAfter(true);
                    ValueAnimator tempChange = ValueAnimator.ofFloat(previousTemp, finalCurrentTemp);
                    tempChange.setDuration(Constants.TEMPERATURE_ANIMATION_DURATION);
                    tempChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            tvTemp.setText(UnitConverter.convertToTemperatureUnitClean((float) animation.getAnimatedValue(), currentTempUnit));
                        }
                    });

                    ValueAnimator tempColorChange = ValueAnimator.ofObject(new ArgbEvaluator(), previousTempColor, newTempColor);
                    tempColorChange.setDuration(Constants.TEMPERATURE_ANIMATION_DURATION);
                    tempColorChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int color = (int) animation.getAnimatedValue();
                            Drawable pointerBackground = DrawableCompat.wrap(Objects.requireNonNull(AppCompatResources.getDrawable(getActivity(), R.drawable.temperature_pointer)));
                            pointerBackground.setTint(color);
                            tempPointer.setBackground(pointerBackground);
                            tvTemp.setTextColor(color);
                            tvDescription.setTextColor(color);
                        }
                    });
                    tempChange.start();
                    tempColorChange.start();
                    tvTemp.startAnimation(tempSlideAnimation);
                    tempPointer.startAnimation(pointerSlideAnimation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateDailyForecastData() {
        DailyForecastAdapter adapter = new DailyForecastAdapter(getActivity(), currentWeather.getDaily());
        rvDailyForecast.setAdapter(adapter);
    }

    private void updateUvData() {
        String uvSummary;
        if (currentWeather.getCurrently().getUvIndex() == 0) {
            uvSummary = getResources().getString(R.string.uv_summary_0);
        } else if (currentWeather.getCurrently().getUvIndex() < 3) {
            uvSummary = getResources().getString(R.string.uv_summary_3);
        } else if (currentWeather.getCurrently().getUvIndex() < 6) {
            uvSummary = getResources().getString(R.string.uv_summary_6);
        } else if (currentWeather.getCurrently().getUvIndex() < 8) {
            uvSummary = getResources().getString(R.string.uv_summary_8);
        } else if (currentWeather.getCurrently().getUvIndex() < 11) {
            uvSummary = getResources().getString(R.string.uv_summary_11);
        } else {
            uvSummary = getResources().getString(R.string.uv_summary_11_above);
        }
        String uvValueText = String.valueOf(Math.round(currentWeather.getCurrently().getUvIndex()));
        tvUvIndex.setText(uvValueText);
        tvUvSummary.setText(uvSummary);
    }

    private void updatePrecipitationData() {
        String type = currentWeather.getDaily().getData().get(0).getPrecipType();
        float value = currentWeather.getCurrently().getPrecipProbability();
        String valueString = (int) (value * 100) + "%";
        tvPrecipitation.setText(valueString);
        if (type != null) {
            icPrecipitationType.setImageResource(type.equals("rain") ? R.drawable.ic_rain_chance : R.drawable.ic_snow_chance);
        }
    }

    private void updateHumidityData() {
        String humidityText = Math.round(currentWeather.getCurrently().getHumidity() * 100) + "%";
        tvHumidity.setText(humidityText);
    }

    public void updateCurrentLocationName(String locationName) {
        if (this.tvLocation != null) {
            this.tvLocation.setText(locationName);
        }
    }

    public void updateExplicitSetting() {
        quoteGenerator.updateHomeScreenQuote(currentWeather);
    }

    public void setOnRefreshListener(OnLayoutRefreshListener callback) {
        this.callback = callback;
    }

    private void updateQuote(Quote quote) {
        tvBigText.setText(quote.getMain());
        tvSmallText.setText(quote.getSub());
    }

    public void updatePhotoDetail(Unsplash unsplash) {
        String photoTitle = unsplash.description != null ? unsplash.description.substring(0, 1).toUpperCase() + unsplash.description.substring(1) : getResources().getString(R.string.photo_name_na);
        String userProfileLink = unsplash.user.links.html;
        String userName = unsplash.user.name;
        String cameraMaker = unsplash.exif.make;
        String cameraModel = unsplash.exif.model != null ? unsplash.exif.model : getResources().getString(R.string.not_available);
        if (cameraMaker != null) {
            if (cameraMaker.toLowerCase().contains(Constants.DUPLICATE_NAME_CAMERA_MAKER[0].toLowerCase())
                    || cameraMaker.toLowerCase().contains(Constants.DUPLICATE_NAME_CAMERA_MAKER[1].toLowerCase())) {
                cameraMaker = "";
            }
        } else {
            cameraMaker = "";
        }
        String camera = cameraMaker + " " + cameraModel;
        String aperture = unsplash.exif.aperture != null ? Constants.APERTURE_PREFIX + unsplash.exif.aperture : getResources().getString(R.string.not_available);
        String focalLength = unsplash.exif.focalLength != null ? unsplash.exif.focalLength + Constants.FOCAL_LENGTH_SUFFIX : getResources().getString(R.string.not_available);
        String iso = unsplash.exif.iso != null ? String.valueOf(unsplash.exif.iso) : getResources().getString(R.string.not_available);
        String exposureTIme = unsplash.exif.exposureTime != null ? unsplash.exif.exposureTime : getResources().getString(R.string.not_available);

        tvPhotoTitle.setText(photoTitle);

        tvPhotoBy.setText(TextUtil.getReferralHtml(Objects.requireNonNull(getActivity()), userProfileLink, userName));
        tvCamera.setText(camera);
        tvAperture.setText(aperture);
        tvFocalLength.setText(focalLength);
        tvIso.setText(iso);
        tvExposureTime.setText(exposureTIme);
        icCamera.setImageResource(getCameraIcon(cameraMaker));
    }

    private int getCameraIcon(String maker) {
        if (maker.equals("")) {
            return R.drawable.ic_camera;
        }

        for (String brand : Constants.DSLR_MAKERS) {
            if (maker.toLowerCase().contains(brand.toLowerCase())) {
                return R.drawable.ic_camera;
            }
        }

        for (String brand : Constants.DRONE_MAKERS) {
            if (maker.toLowerCase().contains(brand.toLowerCase())) {
                return R.drawable.ic_drone_b;
            }
        }

        if (maker.equalsIgnoreCase(Constants.APPLE_CAMERA)) {
            return R.drawable.ic_iphone_b;
        }
        return R.drawable.ic_android_phone_b;
    }


    private void openPhotoDetailBox() {
        ExpandCollapseAnimation.expand(photoDetailsLayout);
        photoDetailsShowing = true;
        iconInfo.setVisibility(View.INVISIBLE);
    }


    public void closePhotoDetailBox() {
        ExpandCollapseAnimation.collapse(photoDetailsLayout);
        photoDetailsShowing = false;
        iconInfo.setVisibility(View.VISIBLE);
    }

    public void hideShareIcon() {
        btnShare.setVisibility(View.INVISIBLE);
        iconInfo.setVisibility(View.INVISIBLE);
    }

    public void showShareIcon() {
        btnShare.setVisibility(View.VISIBLE);
        if (!PreferencesUtil.getBackgroundSetting(Objects.requireNonNull(getActivity())).equals(PreferencesUtil.BACKGROUND_COLOR)) {
            iconInfo.setVisibility(View.VISIBLE);
        } else {
            iconInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMoved(float y) {
        float margin = getResources().getDimension(R.dimen.margin_8);
        tvSmallText.animate()
                .y(y - margin - tvSmallText.getHeight())
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(2 * MovableConstrainLayout.SNAP_DURATION)
                .start();

    }

    @Override
    public void updateScreenQuote(Quote randomQuote) {
        updateQuote(randomQuote);
    }

    public interface OnLayoutRefreshListener {
        void updateData();
    }

}
