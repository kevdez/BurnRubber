package com.raildeliveryservices.burnrubber;

import android.app.ActionBar;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setApplicationName();
    }

    private void setApplicationName() {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = packageManager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersion = info != null ? info.versionName : "";
        String appName = getString(R.string.app_name) + " " + appVersion;

        //Set application name in action bar
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(appName);
    }
}