package com.OdiousPanda.rainbow.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.OdiousPanda.rainbow.MainFragments.DetailsFragment;
import com.OdiousPanda.rainbow.MainFragments.HomeScreenFragment;
import com.OdiousPanda.rainbow.MainFragments.SettingFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private HomeScreenFragment homeScreenFragment;
    private SettingFragment settingFragment;
    private DetailsFragment detailsFragment;

    public SectionsPagerAdapter(FragmentManager fm, HomeScreenFragment hf, SettingFragment sf, DetailsFragment df) {
        super(fm);
        homeScreenFragment = hf;
        settingFragment = sf;
        detailsFragment = df;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return settingFragment;

            case 2:
                return detailsFragment;

            case 1:
            default:
                return homeScreenFragment;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

}
