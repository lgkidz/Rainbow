package com.OdiousPanda.rainbow.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.OdiousPanda.rainbow.WelcomeFragments.WelcomeFragment1;
import com.OdiousPanda.rainbow.WelcomeFragments.WelcomeFragment2;
import com.OdiousPanda.rainbow.WelcomeFragments.WelcomeFragment3;

public class WelcomePagerAdapter extends FragmentPagerAdapter {

    public WelcomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;
        switch (position) {
            case 0: {
                f = new WelcomeFragment1();
                break;
            }
            case 1: {
                f = new WelcomeFragment2();
                break;
            }
            case 2: {
                f = new WelcomeFragment3();
                break;
            }
            default: {
                f = new WelcomeFragment1();
                break;
            }
        }
        return f;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
