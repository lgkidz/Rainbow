package com.OdiousPanda.thefweather.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.OdiousPanda.thefweather.MainFragments.DetailsFragment;
import com.OdiousPanda.thefweather.MainFragments.HomeScreenFragment;
import com.OdiousPanda.thefweather.MainFragments.SettingFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;
        switch (position) {
            case 0:
                f = SettingFragment.getInstance();
                break;

            case 1:
                f = HomeScreenFragment.getInstance();
                break;

            case 2:
                f = DetailsFragment.getInstance();
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
