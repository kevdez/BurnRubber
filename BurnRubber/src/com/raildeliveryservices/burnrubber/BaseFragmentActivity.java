package com.raildeliveryservices.burnrubber;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
