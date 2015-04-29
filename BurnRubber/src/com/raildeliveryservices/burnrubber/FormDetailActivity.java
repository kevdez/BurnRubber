package com.raildeliveryservices.burnrubber;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.raildeliveryservices.burnrubber.fragments.FormDetailFragment;

public class FormDetailActivity extends BaseAuthActivity implements FormDetailFragment.Callbacks {

    private FragmentManager _fm;
    private FragmentTransaction _ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        Bundle bundle = getIntent().getExtras();

        Fragment f = new FormDetailFragment();

        if (bundle != null) {
            f.setArguments(bundle);
        }

        _fm = getFragmentManager();
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
