package com.raildeliveryservices.burnrubber;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.raildeliveryservices.burnrubber.adapters.AppFragmentPagerAdapter;
import com.raildeliveryservices.burnrubber.data.MessageAlert;
import com.raildeliveryservices.burnrubber.fragments.OrderListFragment;
import com.raildeliveryservices.burnrubber.utils.Services;
import com.raildeliveryservices.burnrubber.utils.Utils;

public class MainActivity extends BaseAuthActivity implements OrderListFragment.Callbacks, LoaderManager.LoaderCallbacks<Cursor>, ActionBar.TabListener, ViewPager.OnPageChangeListener {
    private final int LOADER_MESSAGE_ALERT = 1;
    private ActionBar.Tab mMsgTab;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Utils.loadRuntimeSetting(this);
        Services.startAll(this);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        setupViewPager();


        //Setup action bar tab
        mMsgTab = actionBar.newTab().setText(getString(R.string.tab_message)).setTabListener(this);
        actionBar.addTab(mMsgTab);

        ActionBar.Tab orderTab = actionBar.newTab().setText(getString(R.string.tab_orders)).setTabListener(this);
        actionBar.addTab(orderTab);

        ActionBar.Tab orderHistoryTab = actionBar.newTab().setText(getString(R.string.tab_orders_history)).setTabListener(this);
        actionBar.addTab(orderHistoryTab);

        ActionBar.Tab formTab = actionBar.newTab().setText(getString(R.string.tab_forms)).setTabListener(this);
        actionBar.addTab(formTab);
        actionBar.setSelectedNavigationItem(1);

        //Loading message alert
        startLoader();
    }

    private void setupViewPager() {
        //Setup view pager
        mViewPager = (ViewPager) findViewById(R.id.app_pager);
        AppFragmentPagerAdapter appFragmentPagerAdapter = new AppFragmentPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(appFragmentPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    private void startLoader() {
        Loader<Cursor> messageAlertLoader = getLoaderManager().getLoader(LOADER_MESSAGE_ALERT);
        if (messageAlertLoader != null && !messageAlertLoader.isReset()) {
            getLoaderManager().restartLoader(LOADER_MESSAGE_ALERT, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_MESSAGE_ALERT, null, this);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Services.startAll(this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MESSAGE_ALERT) {
            String[] projection = new String[]{MessageAlert.Columns._ID, MessageAlert.Columns.DRIVER_NO, MessageAlert.Columns.NEW_MESSAGE_COUNT};
            String selection = MessageAlert.Columns.DRIVER_NO + " = " + Utils.getDriverNo(this);
            return new CursorLoader(this, MessageAlert.CONTENT_URI, projection, selection, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (loader.getId() == LOADER_MESSAGE_ALERT) {
            if (cursor.moveToFirst()) {
                int newMessageCount = cursor.getInt(cursor.getColumnIndex(MessageAlert.Columns.NEW_MESSAGE_COUNT));
                if (newMessageCount > 0) {
                    if (getActionBar().getSelectedTab() != mMsgTab) {
                        mMsgTab.setText("MSG(" + newMessageCount + ")");
                        mMsgTab.setIcon(R.drawable.ic_new_incoming);
                    }
                    Utils.showMessage(this, String.format("You have %s new message(s)", newMessageCount));
                } else {
                    mMsgTab.setText("MSG");
                    mMsgTab.setIcon(null);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        getActionBar().setSelectedNavigationItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}