package com.raildeliveryservices.burnrubber.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.data.LegOutbound;

public class LegOutboundFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_LEG_OUTBOUND = -1;
	
	private Activity _activity;
	private long _legOutboundId;
	private long _legId;
	private Button _returnButton;
	private EditText _piecesEditText;
	private EditText _weightEditText;
	private EditText _sealEditText;
	private EditText _destinationEditText;
	private EditText _bolEditText;
	private EditText _bolPagesEditText;
	private EditText _palletsEditText;
	private EditText _commodityEditText;
	private Callbacks _callbacks;
	
	public interface Callbacks {
		public void onLegOutboundReturnButtonClick();
	}
	
	public LegOutboundFragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		_activity = getActivity();
		
		_returnButton = (Button) _activity.findViewById(R.id.returnButton);
		_returnButton.setOnClickListener(_buttonListener);
		
		_piecesEditText = (EditText) _activity.findViewById(R.id.piecesEditText);
		_weightEditText = (EditText) _activity.findViewById(R.id.weightEditText);
		_sealEditText = (EditText) _activity.findViewById(R.id.sealEditText);
		_destinationEditText = (EditText) _activity.findViewById(R.id.destinationEditText);
		_bolEditText = (EditText) _activity.findViewById(R.id.bolEditText);
		_bolPagesEditText = (EditText) _activity.findViewById(R.id.bolPagesEditText);
		_palletsEditText = (EditText) _activity.findViewById(R.id.palletsEditText);
		_commodityEditText = (EditText) _activity.findViewById(R.id.commodityEditText);
		
		setFieldMaxLength(_piecesEditText, 5);
		setFieldMaxLength(_weightEditText, 10);
		setFieldMaxLength(_sealEditText, 10);
		setFieldMaxLength(_destinationEditText, 20);
		setFieldMaxLength(_bolEditText, 10);
		setFieldMaxLength(_bolPagesEditText, 3);
		setFieldMaxLength(_palletsEditText, 3);
		setFieldMaxLength(_commodityEditText, 20);
		
		Bundle bundle = getArguments();
		_legId = bundle.getLong(Constants.BUNDLE_PARAM_LEG_ID);
		
		Loader<Cursor> loader = getLoaderManager().getLoader(LOADER_LEG_OUTBOUND);
		if (loader != null && !loader.isReset()) {
			getLoaderManager().restartLoader(LOADER_LEG_OUTBOUND, null, this);
		} else {
			getLoaderManager().initLoader(LOADER_LEG_OUTBOUND, null, this);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.leg_outbound_fragment, container, false);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			_callbacks = (Callbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement Callbacks");
		}
	}
	
	private void setFieldMaxLength(EditText editText, int length) {
		InputFilter[] inputFilters = new InputFilter[1];
		inputFilters[0] = new InputFilter.LengthFilter(length);
		editText.setFilters(inputFilters);
	}
	
	private void saveRecord() {
		
		Uri uri = null;

		uri = LegOutbound.CONTENT_URI;
		
		ContentValues values = new ContentValues();
		values.put(LegOutbound.Columns.LEG_ID, _legId);
		
		if (!TextUtils.isEmpty(_piecesEditText.getText())) {
			values.put(LegOutbound.Columns.PIECES, Integer.parseInt(_piecesEditText.getText().toString()));
		} else {
			values.putNull(LegOutbound.Columns.PIECES);
		}
		
		if (!TextUtils.isEmpty(_weightEditText.getText())) {
			values.put(LegOutbound.Columns.WEIGHT, Double.parseDouble(_weightEditText.getText().toString()));
		} else {
			values.putNull(LegOutbound.Columns.WEIGHT);
		}
		
		if (!TextUtils.isEmpty(_sealEditText.getText())) {
			values.put(LegOutbound.Columns.SEAL, _sealEditText.getText().toString());
		} else {
			values.putNull(LegOutbound.Columns.SEAL);
		}
		
		if (!TextUtils.isEmpty(_destinationEditText.getText())) {
			values.put(LegOutbound.Columns.DESTINATION, _destinationEditText.getText().toString());
		} else {
			values.putNull(LegOutbound.Columns.DESTINATION);
		}
		
		if (!TextUtils.isEmpty(_bolEditText.getText())) {
			values.put(LegOutbound.Columns.BOL, _bolEditText.getText().toString());
		} else {
			values.putNull(LegOutbound.Columns.BOL);
		}
		
		if (!TextUtils.isEmpty(_bolPagesEditText.getText())) {
			values.put(LegOutbound.Columns.BOL_PAGES, Integer.parseInt(_bolPagesEditText.getText().toString()));
		} else {
			values.putNull(LegOutbound.Columns.BOL_PAGES);
		}
		
		if (!TextUtils.isEmpty(_palletsEditText.getText())) {
			values.put(LegOutbound.Columns.PALLETS, Integer.parseInt(_palletsEditText.getText().toString()));
		} else {
			values.putNull(LegOutbound.Columns.PALLETS);
		}
		
		if (!TextUtils.isEmpty(_commodityEditText.getText())) {
			values.put(LegOutbound.Columns.COMMODITY, _commodityEditText.getText().toString());
		} else {
			values.putNull(LegOutbound.Columns.COMMODITY);
		}
		
		if (_legOutboundId > 0) {
			uri = Uri.withAppendedPath(LegOutbound.CONTENT_URI, String.valueOf(_legOutboundId));
			_activity.getContentResolver().update(uri, values, null, null);
		} else {
			_activity.getContentResolver().insert(uri, values);
		}
	}
	
	private OnClickListener _buttonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.returnButton:
					saveRecord();
					_callbacks.onLegOutboundReturnButtonClick();
					break;
			}
		}
	};

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		
		Uri uri = LegOutbound.CONTENT_URI;
		String[] projection = { LegOutbound.Columns._ID,
								LegOutbound.Columns.LEG_ID,
								LegOutbound.Columns.PIECES,
								LegOutbound.Columns.WEIGHT,
								LegOutbound.Columns.SEAL,
								LegOutbound.Columns.DESTINATION,
								LegOutbound.Columns.BOL,
								LegOutbound.Columns.BOL_PAGES,
								LegOutbound.Columns.PALLETS,
								LegOutbound.Columns.COMMODITY };
		String selection = LegOutbound.Columns.LEG_ID + " = " + _legId;
		
		return new CursorLoader(_activity, uri, projection, selection, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			_legOutboundId = cursor.getLong(cursor.getColumnIndex(LegOutbound.Columns._ID));
			_piecesEditText.setText(cursor.getString(cursor.getColumnIndex(LegOutbound.Columns.PIECES)));
			_weightEditText.setText(cursor.getString(cursor.getColumnIndex(LegOutbound.Columns.WEIGHT)));
			_sealEditText.setText(cursor.getString(cursor.getColumnIndex(LegOutbound.Columns.SEAL)));
			_destinationEditText.setText(cursor.getString(cursor.getColumnIndex(LegOutbound.Columns.DESTINATION)));
			_bolEditText.setText(cursor.getString(cursor.getColumnIndex(LegOutbound.Columns.BOL)));
			_bolPagesEditText.setText(cursor.getString(cursor.getColumnIndex(LegOutbound.Columns.BOL_PAGES)));
			_palletsEditText.setText(cursor.getString(cursor.getColumnIndex(LegOutbound.Columns.PALLETS)));
			_commodityEditText.setText(cursor.getString(cursor.getColumnIndex(LegOutbound.Columns.COMMODITY)));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}
