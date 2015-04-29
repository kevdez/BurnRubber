package com.raildeliveryservices.burnrubber.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by nghia on 04/28/2015.
 */
public class AppFragmentPagerAdapter extends FragmentPagerAdapter {

    public AppFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment visibleFragment = null;
        switch (i) {
            case 0:
                break;
            case 1:
                break;
            case 3:
                break;
            case 4:
                break;

        }

        return visibleFragment;
    }

    @Override
    public int getCount() {
        return 4;//Message, Order, Trip History, Forms
    }
}
