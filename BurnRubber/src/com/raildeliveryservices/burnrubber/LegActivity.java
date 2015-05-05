package com.raildeliveryservices.burnrubber;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.raildeliveryservices.burnrubber.fragments.LegListFragment;
import com.raildeliveryservices.burnrubber.fragments.LegOutboundFragment;
import com.raildeliveryservices.burnrubber.utils.Utils;

public class LegActivity extends BaseAuthActivity implements LegListFragment.Callbacks, LegOutboundFragment.CallBacks {

    private FragmentManager _fm;
    private FragmentTransaction _ft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);

        Bundle bundle = getIntent().getExtras();
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
    public boolean onNavigateUp() {
        if (_fm.getBackStackEntryCount() > 0) {
            _fm.popBackStackImmediate();
            return true;
        } else {
            return super.onNavigateUp();
        }
    }

    @Override
    public void onOutboundFormClick(long legId, int fileNo) {

        if (!Utils.isUserOnline(this)) {
            Utils.showMessage(this, "You must be online to complete this action");
            return;
        }
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

    @Override
    public void onLegOutboundSendButtonClick() {
        _fm.popBackStackImmediate();
    }
}
