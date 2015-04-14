package com.raildeliveryservices.burnrubber;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.raildeliveryservices.burnrubber.fragments.FormFragment;

public class FormActivity extends BaseFragmentActivity implements FormFragment.Callbacks {

    private FragmentManager _fm;
    private FragmentTransaction _ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Bundle bundle = getIntent().getExtras();

        Fragment f = new FormFragment();

        if (bundle != null) {
            f.setArguments(bundle);
        }

        _fm = getSupportFragmentManager();
        _ft = _fm.beginTransaction();
        _ft.replace(R.id.contentFrameLayout, f);
        _ft.commit();
    }

    @Override
    public void onFormSendButtonClick() {
        finish();
    }

    @Override
    public void onFormCancelButtonClick() {
        finish();
    }
}
