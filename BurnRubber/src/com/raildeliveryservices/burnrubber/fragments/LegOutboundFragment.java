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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.data.LegOutbound;
import com.raildeliveryservices.burnrubber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class LegOutboundFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = LegOutboundFragment.class.getSimpleName();

    private static final int LOADER_LEG_OUTBOUND = -1;

    private Activity _activity;
    private long _legOutboundId;
    private int _fileNo;
    private long _legId;
    private Button _sendButton;
    private EditText _piecesEditText;
    private EditText _weightEditText;
    private EditText _sealEditText;
    private EditText _destinationEditText;
    private EditText _bolEditText;
    private EditText _bolPagesEditText;
    private EditText _palletsEditText;
    private EditText _commodityEditText;
    private CallBacks mCallBack;
    private OnClickListener _buttonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sendButton:
                    saveRecord();
                    mCallBack.onLegOutboundSendButtonClick();
                    break;
            }
        }
    };

    public LegOutboundFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _activity = getActivity();

        _sendButton = (Button) _activity.findViewById(R.id.sendButton);
        _sendButton.setOnClickListener(_buttonListener);

        _piecesEditText = (EditText) _activity.findViewById(R.id.piecesEditText);
        _weightEditText = (EditText) _activity.findViewById(R.id.weightEditText);
        _sealEditText = (EditText) _activity.findViewById(R.id.sealEditText);
        _destinationEditText = (EditText) _activity.findViewById(R.id.destinationEditText);
        _bolEditText = (EditText) _activity.findViewById(R.id.bolEditText);
        _bolPagesEditText = (EditText) _activity.findViewById(R.id.bolPagesEditText);
        _palletsEditText = (EditText) _activity.findViewById(R.id.palletsEditText);
        _commodityEditText = (EditText) _activity.findViewById(R.id.commodityEditText);

        mCallBack = (CallBacks) _activity;
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
        _fileNo = bundle.getInt(Constants.BUNDLE_PARAM_FILE_NO);

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
        Uri uri = LegOutbound.CONTENT_URI;

        JSONObject json = new JSONObject();
        try {
            json.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_activity));
            json.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
            json.accumulate(WebServiceConstants.FIELD_FILE_NO, _fileNo);
            json.accumulate(WebServiceConstants.FIELD_FORM_NAME, "OUTBOUND LOAD");
            json.accumulate(WebServiceConstants.FIELD_LEG_NO, (int) _legId);
            json.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ClientDateFormat));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        ContentValues values = new ContentValues();
        values.put(LegOutbound.Columns.LEG_ID, _legId);

        if (!TextUtils.isEmpty(_piecesEditText.getText())) {
            values.put(LegOutbound.Columns.PIECES, Integer.parseInt(_piecesEditText.getText().toString()));
            try {
                json.remove(WebServiceConstants.FIELD_LABEL);
                json.remove(WebServiceConstants.FIELD_MESSAGE_TEXT);
                json.accumulate(WebServiceConstants.FIELD_LABEL, "Pieces");
                json.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, _piecesEditText.getText().toString());

                Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            values.putNull(LegOutbound.Columns.PIECES);
        }

        if (!TextUtils.isEmpty(_weightEditText.getText())) {
            values.put(LegOutbound.Columns.WEIGHT, Double.parseDouble(_weightEditText.getText().toString()));
            try {
                json.remove(WebServiceConstants.FIELD_LABEL);
                json.remove(WebServiceConstants.FIELD_MESSAGE_TEXT);
                json.accumulate(WebServiceConstants.FIELD_LABEL, "Weight");
                json.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, _weightEditText.getText().toString());

                Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            values.putNull(LegOutbound.Columns.WEIGHT);
        }

        if (!TextUtils.isEmpty(_sealEditText.getText())) {
            values.put(LegOutbound.Columns.SEAL, _sealEditText.getText().toString());
            try {
                json.remove(WebServiceConstants.FIELD_LABEL);
                json.remove(WebServiceConstants.FIELD_MESSAGE_TEXT);
                json.accumulate(WebServiceConstants.FIELD_LABEL, "Seal");
                json.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, _sealEditText.getText().toString());

                Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            values.putNull(LegOutbound.Columns.SEAL);
        }

        if (!TextUtils.isEmpty(_destinationEditText.getText())) {
            values.put(LegOutbound.Columns.DESTINATION, _destinationEditText.getText().toString());
            try {
                json.remove(WebServiceConstants.FIELD_LABEL);
                json.remove(WebServiceConstants.FIELD_MESSAGE_TEXT);
                json.accumulate(WebServiceConstants.FIELD_LABEL, "Destination");
                json.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, _destinationEditText.getText().toString());

                Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            values.putNull(LegOutbound.Columns.DESTINATION);
        }

        if (!TextUtils.isEmpty(_bolEditText.getText())) {
            values.put(LegOutbound.Columns.BOL, _bolEditText.getText().toString());
            try {
                json.remove(WebServiceConstants.FIELD_LABEL);
                json.remove(WebServiceConstants.FIELD_MESSAGE_TEXT);
                json.accumulate(WebServiceConstants.FIELD_LABEL, "BOL #");
                json.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, _bolEditText.getText().toString());

                Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            values.putNull(LegOutbound.Columns.BOL);
        }

        if (!TextUtils.isEmpty(_bolPagesEditText.getText())) {
            values.put(LegOutbound.Columns.BOL_PAGES, Integer.parseInt(_bolPagesEditText.getText().toString()));
            try {
                json.remove(WebServiceConstants.FIELD_LABEL);
                json.remove(WebServiceConstants.FIELD_MESSAGE_TEXT);
                json.accumulate(WebServiceConstants.FIELD_LABEL, "Pages of BOL");
                json.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, _bolPagesEditText.getText().toString());

                Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            values.putNull(LegOutbound.Columns.BOL_PAGES);
        }

        if (!TextUtils.isEmpty(_palletsEditText.getText())) {
            values.put(LegOutbound.Columns.PALLETS, Integer.parseInt(_palletsEditText.getText().toString()));
            try {
                json.remove(WebServiceConstants.FIELD_LABEL);
                json.remove(WebServiceConstants.FIELD_MESSAGE_TEXT);
                json.accumulate(WebServiceConstants.FIELD_LABEL, "Pallets");
                json.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, _palletsEditText.getText().toString());

                Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            values.putNull(LegOutbound.Columns.PALLETS);
        }

        if (!TextUtils.isEmpty(_commodityEditText.getText())) {
            values.put(LegOutbound.Columns.COMMODITY, _commodityEditText.getText().toString());
            try {
                json.remove(WebServiceConstants.FIELD_LABEL);
                json.remove(WebServiceConstants.FIELD_MESSAGE_TEXT);
                json.accumulate(WebServiceConstants.FIELD_LABEL, "Commodity");
                json.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, _commodityEditText.getText().toString());

                Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
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

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        Uri uri = LegOutbound.CONTENT_URI;
        String[] projection = {LegOutbound.Columns._ID,
                LegOutbound.Columns.LEG_ID,
                LegOutbound.Columns.PIECES,
                LegOutbound.Columns.WEIGHT,
                LegOutbound.Columns.SEAL,
                LegOutbound.Columns.DESTINATION,
                LegOutbound.Columns.BOL,
                LegOutbound.Columns.BOL_PAGES,
                LegOutbound.Columns.PALLETS,
                LegOutbound.Columns.COMMODITY};
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

    public static interface CallBacks {
        public void onLegOutboundSendButtonClick();
    }
}
