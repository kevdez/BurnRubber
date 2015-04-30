package com.raildeliveryservices.burnrubber;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.raildeliveryservices.burnrubber.fragments.LegListFragment;
import com.raildeliveryservices.burnrubber.fragments.LegOutboundFragment;

public class LegActivity extends BaseAuthActivity implements LegListFragment.Callbacks, LegOutboundFragment.Callbacks {

    private long _orderId;
    private FragmentManager _fm;
    private FragmentTransaction _ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);

        Bundle bundle = getIntent().getExtras();
        _orderId = bundle.getLong(Constants.BUNDLE_PARAM_ORDER_ID);

        _fm = getSupportFragmentManager();

        Fragment f = new LegListFragment();
        f.setArguments(bundle);

        _ft = _fm.beginTransaction();
        _ft.replace(R.id.contentFrameLayout, f);
        _ft.commit();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Back");
    }


    @Override
    public void onLegOutboundReturnButtonClick() {
        _fm.popBackStackImmediate();
    }

    @Override
    public void onOutboundFormClick(long legId, int fileNo) {

        Fragment f = new LegOutboundFragment();
        Bundle b = new Bundle();
        b.putLong(Constants.BUNDLE_PARAM_LEG_ID, legId);
        b.putInt(Constants.BUNDLE_PARAM_FILE_NO, fileNo);
        f.setArguments(b);

        _ft = _fm.beginTransaction();
        _ft.addToBackStack(null);
        _ft.replace(R.id.contentFrameLayout, f);
        _ft.commit();
    }
}
