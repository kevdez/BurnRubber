package com.raildeliveryservices.burnrubber;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.raildeliveryservices.burnrubber.fragments.OrderListFragment;
import com.raildeliveryservices.burnrubber.utils.Services;
import com.raildeliveryservices.burnrubber.utils.Utils;

public class OrderActivity extends BaseLoggedInActivity implements OrderListFragment.Callbacks {

    private static Intent msgIntent;
    private FragmentManager _fm;
    private FragmentTransaction _ft;

    public static Intent getOriginalMsgIntent() {
        return msgIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Utils.loadRuntimeSetting(this);
        Services.startAll(this);

        _fm = getFragmentManager();

        msgIntent = new Intent(this, MessageActivity.class);
        loadOrders();
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Services.startAll(this);
    }

    private void loadOrders() {

        Fragment f = new OrderListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_PARAM_TRIP_HISTORY, false);
        f.setArguments(bundle);

        _ft = _fm.beginTransaction();
        _ft.replace(R.id.contentFrameLayout, f);
        _ft.commit();
    }

    @Override
    public void onOrderListItemClick(long orderId, boolean readOnly) {

        Bundle bundle = new Bundle();
        bundle.putLong(Constants.BUNDLE_PARAM_ORDER_ID, orderId);

        if (!Utils.isUserOnline(this) || readOnly) {
            bundle.putBoolean(Constants.BUNDLE_PARAM_READ_ONLY, true);
        } else {
            bundle.putBoolean(Constants.BUNDLE_PARAM_READ_ONLY, false);
        }

        Intent intent = new Intent(this, LegActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}