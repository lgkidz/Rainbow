package com.OdiousPanda.thefweather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.OdiousPanda.thefweather.MainFragments.ForecastFragment;
import com.OdiousPanda.thefweather.MainFragments.HomeScreenFragment;
import com.OdiousPanda.thefweather.MainFragments.SettingFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private final int SETTING_FRAGMENT_POSITION = 0;
    private final int HOME_FRAGMENT_POSITION = 1;
    private final int FORECAST_FRAGMENT_POSITION = 2;

    @Override
    public Fragment getItem(int position) {
        Fragment f;
        switch (position){
            case SETTING_FRAGMENT_POSITION:
                f = SettingFragment.getInstance();
                break;

            case HOME_FRAGMENT_POSITION:
                f = HomeScreenFragment.getInstance();
                break;

            case FORECAST_FRAGMENT_POSITION:
                f = ForecastFragment.getInstance();
                break;

            default:
                f = new HomeScreenFragment();
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

}
