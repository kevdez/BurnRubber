package com.raildeliveryservices.burnrubber;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.raildeliveryservices.burnrubber.data.MessageAlert;
import com.raildeliveryservices.burnrubber.fragments.FormFragment;
import com.raildeliveryservices.burnrubber.fragments.MessageListFragment;
import com.raildeliveryservices.burnrubber.fragments.OrderListFragment;
import com.raildeliveryservices.burnrubber.utils.Services;
import com.raildeliveryservices.burnrubber.utils.Utils;

public class MainActivity extends BaseAuthActivity implements OrderListFragment.Callbacks, LoaderManager.LoaderCallbacks<Cursor> {
    private final int LOADER_MESSAGE_ALERT = 1;
    private ActionBar.Tab mMsgTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Utils.loadRuntimeSetting(this);
        Services.startAll(this);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        mMsgTab = actionBar.newTab().setText(getString(R.string.tab_message)).setTabListener(new AppTabListener<MessageListFragment>(this, getString(R.string.tab_message), MessageListFragment.class));
        actionBar.addTab(mMsgTab);

        ActionBar.Tab orderTab = actionBar.newTab().setText(getString(R.string.tab_orders)).setTabListener(new AppTabListener<OrderListFragment>(this, getString(R.string.tab_orders), OrderListFragment.class));
        actionBar.addTab(orderTab);

        ActionBar.Tab orderHistoryTab = actionBar.newTab().setText(getString(R.string.tab_orders_history)).setTabListener(new AppTabListener<OrderListFragment>(this, getString(R.string.tab_orders_history), OrderListFragment.class));
        actionBar.addTab(orderHistoryTab);

        ActionBar.Tab formTab = actionBar.newTab().setText(getString(R.string.tab_forms)).setTabListener(new AppTabListener<FormFragment>(this, getString(R.string.tab_forms), FormFragment.class));
        actionBar.addTab(formTab);
        actionBar.setSelectedNavigationItem(1);

        startLoader();
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

    private class AppTabListener<T extends Fragment> implements ActionBar.TabListener {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private Fragment mFragment;

        public AppTabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;

            //Check for previously attached fragments.
            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null) {
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }


        @Override
        public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName());

                if (mTag == getString(R.string.tab_orders)) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.BUNDLE_PARAM_TRIP_HISTORY, false);
                    mFragment.setArguments(bundle);
                } else if (mTag == getString(R.string.tab_orders_history)) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.BUNDLE_PARAM_TRIP_HISTORY, true);
                    mFragment.setArguments(bundle);
                }

                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            Log.d(TAG, "onTabUnselected");
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

        }
    }
}