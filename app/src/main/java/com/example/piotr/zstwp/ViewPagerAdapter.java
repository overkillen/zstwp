package com.example.piotr.zstwp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        // Ktora zakladka (fragment) zostanie otwarta
        switch (position) {
            case 0:
                return new WfmFragment();
            case 1:
                return new FmFragment();
        }

        return new WfmFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

}