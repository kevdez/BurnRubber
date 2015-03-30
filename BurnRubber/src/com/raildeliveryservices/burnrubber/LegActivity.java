package com.raildeliveryservices.burnrubber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.raildeliveryservices.burnrubber.fragments.LegListFragment;
import com.raildeliveryservices.burnrubber.fragments.LegOutboundFragment;

public class LegActivity extends BaseFragmentActivity implements LegListFragment.Callbacks, LegOutboundFragment.Callbacks {

	private long _orderId;
	private FragmentManager _fm;
	private FragmentTransaction _ft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		Bundle bundle = getIntent().getExtras();
		_orderId = bundle.getLong(Constants.BUNDLE_PARAM_ORDER_ID);
		
		_fm = getSupportFragmentManager();
		
		Fragment f = new LegListFragment();
		f.setArguments(bundle);
		
		_ft = _fm.beginTransaction();
		_ft.replace(R.id.contentFrameLayout, f);
		_ft.commit();
	}

	@Override
	public void onMessageButtonClick() {

        startActivity(new Intent(OrderActivity.getOriginalMsgIntent()));
	}

	@Override
	public void onDirectionsButtonClick() {
		// TODO Auto-generated method stub
		
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

	@Override
	public void onReturnButtonClick() {
		finish();
	}

	@Override
	public void onOrderImageButtonClick() {
		/*
		Fragment f = new OrderImageFragment();
		Bundle b = new Bundle();
		b.putLong(Constants.BUNDLE_PARAM_ORDER_ID, _orderId);
		f.setArguments(b);

		_ft = _fm.beginTransaction();
		_ft.addToBackStack(null);
		_ft.replace(R.id.contentFrameLayout, f);
		_ft.commit();
		*/
	}
}
