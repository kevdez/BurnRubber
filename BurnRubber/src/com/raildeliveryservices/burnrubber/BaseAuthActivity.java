package com.raildeliveryservices.burnrubber;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.raildeliveryservices.burnrubber.utils.Services;
import com.raildeliveryservices.burnrubber.utils.Utils;

/**
 * Created by nghia on 04/27/2015.
 */
public class BaseAuthActivity extends FragmentActivity {
    protected final String TAG = this.getClass().getSimpleName();
    private Button mOnlineActionButton;
    private ImageView mOnlineIndicator;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d(this.getClass().getSimpleName(), "onCreate");
        //Set driver number as app title
        getActionBar().setTitle(Utils.getDriverNo(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_actions, menu);
        MenuItem onlineMenuItem = menu.findItem(R.id.action_online_status);
        View actionView = onlineMenuItem.getActionView();
        mOnlineActionButton = (Button) actionView.findViewById(R.id.online_action_button);
        mOnlineIndicator = (ImageView) actionView.findViewById(R.id.online_indicator);
        mOnlineActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isUserOnline(BaseAuthActivity.this)) {
                    //User is currently online
                    setUserOffline();
                } else {
                    //User is currently offline.
                    final String[] choices = new String[]{getResources().getString(R.string.intermodal), getResources().getString(R.string.crossdock)};
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(BaseAuthActivity.this);
                    alertBuilder.setTitle(getString(R.string.select_online_system));
                    alertBuilder.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setUserOnline(choices[which]);
                            dialog.cancel();
                        }
                    });
                    alertBuilder.create().show();
                }
            }
        });
        loadOnlineStatus();
        Log.d(TAG, "onCreateOptionsMenu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        loadOnlineStatus();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_log_off:
                logOff();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logOff() {
        Utils.setUserLoggedIn(this, false);
        setUserOffline();
        Services.stopAll(this);
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            ;
        }

        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        System.exit(0);
    }

    private void loadOnlineStatus() {
        boolean isOnline = Utils.isUserOnline(this);
        if (isOnline) {
            setUserOnline(Utils.getDriverOnlineSystem(this));
        } else {
            setUserOffline();
        }
    }

    protected void setUserOnline(String onlineChoice) {
        mOnlineActionButton.setText("Online " + onlineChoice);
        mOnlineActionButton.setTextColor(getResources().getColor(R.color.green));
        mOnlineIndicator.setImageResource(R.drawable.ic_action_online);

        Utils.setUserOnline(this, true);
        Utils.setDriverOnlineSystem(this, onlineChoice);
        Utils.sendUserOnlineToServer(this, true, onlineChoice);
    }

    protected void setUserOffline() {
        mOnlineActionButton.setText(getString(R.string.you_are_offline));
        mOnlineActionButton.setTextColor(getResources().getColor(R.color.red));
        mOnlineIndicator.setImageResource(R.drawable.ic_action_offline);

        Utils.setUserOnline(this, false);
        Utils.setDriverOnlineSystem(this, "");
        Utils.sendUserOnlineToServer(this, false, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadOnlineStatus();
        Log.d(TAG, "onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
