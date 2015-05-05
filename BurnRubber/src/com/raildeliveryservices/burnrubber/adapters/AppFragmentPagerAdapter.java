package com.raildeliveryservices.burnrubber.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.fragments.FormFragment;
import com.raildeliveryservices.burnrubber.fragments.MessageListFragment;
import com.raildeliveryservices.burnrubber.fragments.OrderListFragment;

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
        Bundle bundle = new Bundle();
        switch (i) {
            case 0:
                visibleFragment = new MessageListFragment();
                break;
            case 1:
                bundle.putBoolean(Constants.BUNDLE_PARAM_TRIP_HISTORY, false);
                visibleFragment = new OrderListFragment();
                visibleFragment.setArguments(bundle);
                break;
            case 2:
                bundle.putBoolean(Constants.BUNDLE_PARAM_TRIP_HISTORY, true);
                visibleFragment = new OrderListFragment();
                visibleFragment.setArguments(bundle);
                break;
            case 3:
                visibleFragment = new FormFragment();
                break;

        }

        return visibleFragment;
    }

    @Override
    public int getCount() {
        return 4;//Message, Order, Trip History, Forms
    }
}
