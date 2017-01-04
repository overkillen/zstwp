package com.example.piotr.zstwp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        // Ktora zakladka (fragment) zostanie otwarta
        switch (position) {
            case 0:
                return new WfmStartFragment();
            case 1:
                return new FmFragment();
        }

        return new WfmStartFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

}