package com.raildeliveryservices.burnrubber.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.adapters.LegListCursorAdapter;
import com.raildeliveryservices.burnrubber.data.Leg;
import com.raildeliveryservices.burnrubber.data.Order;
import com.raildeliveryservices.burnrubber.data.WebResponse;
import com.raildeliveryservices.burnrubber.tasks.SendRequestAsyncTask;
import com.raildeliveryservices.burnrubber.utils.Utils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LegListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SendRequestAsyncTask.OnRequestCompleted {
    private static final String LOG_TAG = LegListFragment.class.getSimpleName();
    private static final int ACCEPT_FILE_CODE = 1;
    private static final int START_FILE_CODE = 2;
    private static final int LOADER_LEGS = -1;
    private Order mOrder;
    private Activity _activity;
    private LegListCursorAdapter.AdapterCallbacks _listAdapterButtonListener = new LegListCursorAdapter.AdapterCallbacks() {

        @Override
        public void onArriveDepartButtonClick(View v, long legId) {
            if (!Utils.isUserOnline(_activity)) {
                Utils.showMessage(_activity, "You must be online to complete this action");
                return;
            }

            if (!canArriveDepart()) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_activity);
                alertBuilder.setTitle(_activity.getString(R.string.container_chassis_error_title));
                alertBuilder.setMessage(_activity.getString(R.string.container_chassis_error_message));
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton(_activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertBuilder.create().show();

                return;
            }

            SimpleDateFormat dateTime = Constants.ServerDateFormat;
            Uri uri = Uri.withAppendedPath(Leg.CONTENT_URI, String.valueOf(legId));
            ContentValues values = new ContentValues();

            switch (v.getId()) {
                case R.id.arriveFromButton:
                    values.put(Leg.Columns.ARRIVE_FROM_DATE_TIME, dateTime.format(new Date()));
                    sendUpdateToServer(legId, "ARRIVE");
                    break;
                case R.id.departFromButton:
                    values.put(Leg.Columns.DEPART_FROM_DATE_TIME, dateTime.format(new Date()));
                    sendUpdateToServer(legId, "DEPART");
                    break;
                case R.id.arriveToButton:
                    values.put(Leg.Columns.ARRIVE_TO_DATE_TIME, dateTime.format(new Date()));
                    sendUpdateToServer(legId, "ARRIVE");
                    break;
                case R.id.departToButton:
                    values.put(Leg.Columns.DEPART_TO_DATE_TIME, dateTime.format(new Date()));
                    sendUpdateToServer(legId, "DEPART");
                    values.put(Leg.Columns.COMPLETED_FLAG, true);
                    break;
                case R.id.endFileButton:
                    sendUpdateToServer(legId, "END FILE");

                    // renders the leg complete, like in case R.id.departToButton
                    values.put(Leg.Columns.DEPART_TO_DATE_TIME, dateTime.format(new Date()));
                    values.put(Leg.Columns.COMPLETED_FLAG, true);

                    // also renders the ORDER complete
                    Uri uri2 = Uri.withAppendedPath(Order.CONTENT_URI, String.valueOf(mOrder.getId()));
                    ContentValues values2 = new ContentValues();
                    values2.put(Order.Columns.COMPLETED_FLAG, true);
                    _activity.getContentResolver().update(uri2, values2, null, null);

                    mOrderStatus.setText("File completed");
                    mUpdateChassisNoButton.setVisibility(View.GONE);
                    mUpdateContainerNoButton.setVisibility(View.GONE);
                    break;
            }

            _activity.getContentResolver().update(uri, values, null, null);
        }

        /**
         * Sends to the server whether the status is ARRIVE/DEPART/END FILE
         * @param legId the leg number
         * @param driverStatus the value of ARRIVE, DEPART, or END FILE
         */
        private void sendUpdateToServer(long legId, String driverStatus) {
            JSONObject requestJson = new JSONObject();
            try {
                requestJson.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_activity));
                requestJson.accumulate(WebServiceConstants.FIELD_LABEL, driverStatus);
                requestJson.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
                requestJson.accumulate(WebServiceConstants.FIELD_FILE_NO, mOrder.getFileNo());
                requestJson.accumulate(WebServiceConstants.FIELD_LEG_NO, (int) legId);
                requestJson.accumulate(WebServiceConstants.FIELD_FORM_NAME, "CANNED");
                requestJson.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, mOrder.getFileNo());
                requestJson.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ClientDateFormat));

                Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, requestJson);
            } catch (Exception e) {
                Log.e(LOG_TAG, "sendUpdateToServer(): " + e.getMessage());
                Log.e(LOG_TAG, "URL: " + WebServiceConstants.URL_CREATE_MESSAGE);
                Log.e(LOG_TAG, "JSON: " + requestJson.toString());
            }
        }

        @Override
        public void onOutboundFormClick(long legId, int fileNo) {
            _callbacks.onOutboundFormClick(legId, fileNo);
        }

        /**
         * This function is called when an ARRIVE / DEPART / DONE button is pressed again. The first time that button is pressed,
         * the text turns into a time like "4/5/15 8:00am". Pressing that new time button calls this.
         * @param v The view
         * @param legId The leg number
         */
        @Override
        public void onEditArriveDepartButtonClick(View v, long legId) {

            final Dialog dialog = new Dialog(_activity);
            final View buttonView = v;
            final long id = legId;

            dialog.setContentView(R.layout.edit_date_dialog);

            switch (v.getId()) {
                case R.id.arriveFromButton:
                case R.id.arriveToButton:
                    dialog.setTitle(_activity.getString(R.string.edit_arrive_date_time_title));
                    break;
                case R.id.departFromButton:
                case R.id.departToButton:
                    dialog.setTitle(_activity.getString(R.string.edit_depart_date_time_title));
                    break;
            }

            Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
            Button saveButton = (Button) dialog.findViewById(R.id.saveButton);

            cancelButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            saveButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
                    TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);

                    int year = datePicker.getYear();
                    int month = datePicker.getMonth();
                    int day = datePicker.getDayOfMonth();
                    int hour = timePicker.getCurrentHour();
                    int min = timePicker.getCurrentMinute();

                    updateArriveDepartDateTime(buttonView, id, year, month, day, hour, min);

                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    };
    private OnClickListener _buttonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.startFileButton:
                    if (Utils.isUserOnline(_activity)) {
                        startFile();
                    } else {
                        Utils.showMessage(_activity, "You must be online to start the order");
                    }
                    break;
                case R.id.accept_order:
                    acceptOrder();
                    break;
                case R.id.reject_order:
                    rejectOrder();
                    break;
                case R.id.update_container_no:
                    if (Utils.isUserOnline(_activity)) {
                        updateContainerNo();
                    } else {
                        Utils.showMessage(_activity, _activity.getString(R.string.required_online));
                    }
                    break;
                case R.id.update_chassis_no:
                    if (Utils.isUserOnline(_activity)) {
                        updateChassisNo();
                    } else {
                        Utils.showMessage(_activity, _activity.getString(R.string.required_online));
                    }
                    break;
            }
        }
    };
    private boolean mHistoryMode;
    private ExpandableListView _listView;
    private Button _startFileButton, mAcceptButton, mRejectButton, mUpdateContainerNoButton, mUpdateChassisNoButton;
    private View mBottomButtons;
    private TextView _fileNoText;
    private ImageView mHazmatImage;
    private TextView _apptDateText, mOrderStatus;
    private TextView _apptTimeText;
    private TextView _voyageNoText;
    private TextView _moveTypeText;
    private EditText _containerNoEditText;
    private EditText _chassisNoEditText;
    private TextView _commentsText;
    private LegListCursorAdapter _listAdapter;
    private Callbacks _callbacks;

    public LegListFragment() {
    }

    private void updateChassisNo() {
        final EditText updateEditText = new EditText(_activity);
        updateEditText.setText(mOrder.getChassisNo());
        updateEditText.setSelection(updateEditText.getText().length());
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(_activity);
        dialogBuilder.setTitle("Please enter new 'Chassis #'");
        dialogBuilder.setView(updateEditText);
        dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newChassisNo = updateEditText.getText().toString();
                mOrder.setChassisNo(newChassisNo);
                //Update view
                _chassisNoEditText.setText(newChassisNo);
                //Update local database
                ContentValues contentValues = new ContentValues();
                contentValues.put(Order.Columns.CHASSIS_NO, newChassisNo);
                _activity.getContentResolver().update(Uri.withAppendedPath(Order.CONTENT_URI, mOrder.getId()), contentValues, null, null);
                //Send message to the server
                sendContainerOrChassisToServer("CHASSIS", newChassisNo);
                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.create().show();
    }

    private void updateContainerNo() {
        final EditText updateEditText = new EditText(_activity);
        updateEditText.setText(mOrder.getContainerNo());
        updateEditText.setSelection(updateEditText.getText().length());
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(_activity);
        dialogBuilder.setTitle(_activity.getString(R.string.enter_new_container));
        dialogBuilder.setView(updateEditText);
        dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newContainerNo = updateEditText.getText().toString();
                mOrder.setContainerNo(newContainerNo);
                //Update view
                _containerNoEditText.setText(newContainerNo);
                //Update local database
                ContentValues contentValues = new ContentValues();
                contentValues.put(Order.Columns.CONTAINER_NO, newContainerNo);
                _activity.getContentResolver().update(Uri.withAppendedPath(Order.CONTENT_URI, mOrder.getId()), contentValues, null, null);
                //Send message to the server
                sendContainerOrChassisToServer("CONTAINER", newContainerNo);
                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.create().show();
    }

    private void rejectOrder() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(_activity);
        dialogBuilder.setTitle(_activity.getResources().getString(R.string.reject_order_dialog_title));
        dialogBuilder.setMessage(String.format(_activity.getResources().getString(R.string.reject_order_dialog_message), mOrder.getFileNo()));
        dialogBuilder.setPositiveButton(_activity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _activity.getContentResolver().delete(
                        Uri.withAppendedPath(Order.CONTENT_URI, mOrder.getId()), null, null);
                deleteAssociateLegs(mOrder.getFileNo());
                dialog.dismiss();

                JSONObject requestJson = new JSONObject();
                try {
                    requestJson.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_activity));
                    requestJson.accumulate(WebServiceConstants.FIELD_LABEL, "REJECT");
                    requestJson.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
                    requestJson.accumulate(WebServiceConstants.FIELD_FILE_NO, mOrder.getFileNo());
                    requestJson.accumulate(WebServiceConstants.FIELD_FORM_NAME, "CANNED");
                    requestJson.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, mOrder.getFileNo());
                    requestJson.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ClientDateFormat));

                    Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, requestJson);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                    Log.e(LOG_TAG, "URL: " + WebServiceConstants.URL_CREATE_MESSAGE);
                    Log.e(LOG_TAG, "JSON: " + requestJson.toString());
                }

                _activity.finish();
            }
        });
        dialogBuilder.setNegativeButton(_activity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _activity = getActivity();

        _startFileButton = (Button) _activity.findViewById(R.id.startFileButton);
        mAcceptButton = (Button) _activity.findViewById(R.id.accept_order);
        mRejectButton = (Button) _activity.findViewById(R.id.reject_order);
        mUpdateContainerNoButton = (Button) _activity.findViewById(R.id.update_container_no);
        mUpdateChassisNoButton = (Button) _activity.findViewById(R.id.update_chassis_no);
        _fileNoText = (TextView) _activity.findViewById(R.id.fileNoText);
        mHazmatImage = (ImageView) _activity.findViewById(R.id.hazmatImage);
        _apptDateText = (TextView) _activity.findViewById(R.id.apptDateText);
        _apptTimeText = (TextView) _activity.findViewById(R.id.apptTimeText);
        _voyageNoText = (TextView) _activity.findViewById(R.id.voyageNoText);
        _moveTypeText = (TextView) _activity.findViewById(R.id.moveTypeText);
        mOrderStatus = (TextView) _activity.findViewById(R.id.order_status);
        mBottomButtons = _activity.findViewById(R.id.bottomButtons);
        _containerNoEditText = (EditText) _activity.findViewById(R.id.containerNoEditText);
        _chassisNoEditText = (EditText) _activity.findViewById(R.id.chassisNoEditText);
        _containerNoEditText.setKeyListener(null);
        _chassisNoEditText.setKeyListener(null);
        _commentsText = (TextView) _activity.findViewById(R.id.commentsText);
        _startFileButton.setOnClickListener(_buttonListener);
        mAcceptButton.setOnClickListener(_buttonListener);
        mRejectButton.setOnClickListener(_buttonListener);
        mUpdateContainerNoButton.setOnClickListener(_buttonListener);
        mUpdateChassisNoButton.setOnClickListener(_buttonListener);


        mOrder = (Order) getArguments().getSerializable(Constants.BUNDLE_PARAM_SELECTED_ORDER);
        mHistoryMode = getArguments().getBoolean(Constants.BUNDLE_PARAM_TRIP_HISTORY);
        if (mOrder != null) {
            displayOrder();
        }

        _listView = (ExpandableListView) _activity.findViewById(R.id.legList);
        _listView.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
        _listAdapter = new LegListCursorAdapter(_activity, mHistoryMode, mOrder);
        _listAdapter.setButtonListener(_listAdapterButtonListener);
        _listView.setAdapter(_listAdapter);
        _listView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < _listAdapter.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        _listView.collapseGroup(i);
                    }
                }
            }
        });
    }

    private void displayOrder() {
        _fileNoText.setText(mOrder.getFileNo());
        _apptDateText.setText(mOrder.getAppointmentDate());
        _apptTimeText.setText(mOrder.getAppointmentTime());
        _voyageNoText.setText(mOrder.getVoyageNo());
        _moveTypeText.setText(mOrder.getMoveType());
        _containerNoEditText.setText(mOrder.getContainerNo());
        _chassisNoEditText.setText(mOrder.getChassisNo());
        _commentsText.setText(mOrder.getComment());
        mHazmatImage.setVisibility(mOrder.getHazmatFlag() == 1 ? View.VISIBLE : View.GONE);

        mAcceptButton.setVisibility(mOrder.getConfirmFlag() == 1 ? View.GONE : View.VISIBLE);
        mRejectButton.setVisibility(mAcceptButton.getVisibility());

        if (!mHistoryMode) {
            if (mOrder.getConfirmFlag() == 1) {
                if (mOrder.getStartFlag() == 1) {
                    //File is started
                    _startFileButton.setVisibility(View.GONE);
                    mOrderStatus.setVisibility(View.VISIBLE);
                    mBottomButtons.setVisibility(View.VISIBLE);
                    mUpdateContainerNoButton.setVisibility(View.VISIBLE);
                    mUpdateChassisNoButton.setVisibility(View.VISIBLE);
                } else {
                    _startFileButton.setVisibility(View.VISIBLE);
                }
            }
        }

        //Start loader to load legs
        Loader<Cursor> l = getLoaderManager().getLoader(LOADER_LEGS);
        if (l != null && !l.isReset()) {
            getLoaderManager().restartLoader(LOADER_LEGS, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_LEGS, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.leg_list_fragment, container, false);
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

    /**
     * This function gets called when the user is online, confirmed the order, and presses the "Start file" button.
     */
    private void startFile() {
        Uri uri = Uri.withAppendedPath(Order.CONTENT_URI, String.valueOf(mOrder.getId()));
        mOrder.setStartFlag(1);
        ContentValues values = new ContentValues();
        values.put(Order.Columns.STARTED_FLAG, 1);
        _activity.getContentResolver().update(uri, values, null, null);

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_activity));
            requestJson.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
            requestJson.accumulate(WebServiceConstants.FIELD_FILE_NO, mOrder.getFileNo());
            requestJson.accumulate(WebServiceConstants.FIELD_LEG_NO, "1");
            requestJson.accumulate(WebServiceConstants.FIELD_LABEL, "START FILE");
            requestJson.accumulate(WebServiceConstants.FIELD_FORM_NAME, "CANNED");
            requestJson.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, mOrder.getFileNo());
            requestJson.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ClientDateFormat));

            SendRequestAsyncTask sendRequestAsyncTask = new SendRequestAsyncTask(_activity, START_FILE_CODE, this);
            sendRequestAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebServiceConstants.URL_CREATE_MESSAGE, requestJson.toString());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void acceptOrder() {
        try {
            mOrder.setConfirmFlag(1);
            ContentValues values = new ContentValues();
            values.put(Order.Columns.CONFIRMED_FLAG, 1);
            _activity.getContentResolver().update(Uri.withAppendedPath(Order.CONTENT_URI, mOrder.getId()), values, null, null);

            JSONObject requestJson = new JSONObject();
            requestJson.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_activity));
            requestJson.accumulate(WebServiceConstants.FIELD_LABEL, "ACCEPT");
            requestJson.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
            requestJson.accumulate(WebServiceConstants.FIELD_FILE_NO, mOrder.getFileNo());
            requestJson.accumulate(WebServiceConstants.FIELD_FORM_NAME, "CANNED");
            requestJson.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, mOrder.getFileNo());
            requestJson.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ClientDateFormat));

            SendRequestAsyncTask sendRequestAsyncTask = new SendRequestAsyncTask(_activity, ACCEPT_FILE_CODE, this);
            sendRequestAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebServiceConstants.URL_CREATE_MESSAGE, requestJson.toString());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }

    /**
     * This function was supposed to be the check whether the driver entered in container and chassis information.
     *
     * @return boolean whether the arrive and depart buttons are enabled.
     */
    private boolean canArriveDepart() {
        return true;
    }

    // This may require updating the leg to be either NULL or an actual current leg.
    private void sendContainerOrChassisToServer(String label, String value) {
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_activity));
            requestJson.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
            requestJson.accumulate(WebServiceConstants.FIELD_FILE_NO, mOrder.getFileNo());
            requestJson.accumulate(WebServiceConstants.FIELD_LABEL, label);
            requestJson.accumulate(WebServiceConstants.FIELD_FORM_NAME, "CANNED");
            requestJson.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, value);
            requestJson.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ClientDateFormat));

            Utils.sendMessageToServer(_activity, WebServiceConstants.URL_CREATE_MESSAGE, requestJson);
        } catch (Exception e) {
            Log.e(LOG_TAG, "In sendContainerOrChassisToServer(): " + e.getMessage());
            Log.e(LOG_TAG, "URL: " + WebServiceConstants.URL_CREATE_MESSAGE);
            Log.e(LOG_TAG, "JSON: " + requestJson.toString());
        }
    }

    private void updateArriveDepartDateTime(View v, long legId, int year, int month, int day, int hour, int min) {

        SimpleDateFormat dateTimeFormat = Constants.ClientDateFormat;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, min);

        String dateTime = dateTimeFormat.format(cal.getTime());

        Uri uri = Uri.withAppendedPath(Leg.CONTENT_URI, String.valueOf(legId));
        ContentValues values = new ContentValues();

        switch (v.getId()) {
            case R.id.arriveFromButton:
                values.put(Leg.Columns.ARRIVE_FROM_DATE_TIME, dateTime);
                break;
            case R.id.arriveToButton:
                values.put(Leg.Columns.ARRIVE_TO_DATE_TIME, dateTime);
                break;
            case R.id.departFromButton:
                values.put(Leg.Columns.DEPART_FROM_DATE_TIME, dateTime);
                break;
            case R.id.departToButton:
                values.put(Leg.Columns.DEPART_TO_DATE_TIME, dateTime);
                break;
        }

        _activity.getContentResolver().update(uri, values, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_LEGS) {
            Uri uri = Leg.CONTENT_URI;
            String selection = Leg.Columns.ORDER_ID + " = " + mOrder.getId();
            String[] projection = new String[]{Leg.Columns._ID, Leg.Columns.LEG_NO, Leg.Columns.COMPLETED_FLAG};
            String sortOrder = Leg.Columns.LEG_NO;
            return new CursorLoader(_activity, uri, projection, selection, null, sortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == LOADER_LEGS) {
            _listAdapter.setGroupCursor(cursor);
            cursor.moveToFirst();
            int i = 0;
            try {
                do {
                    if (cursor.getInt(cursor.getColumnIndex(Leg.Columns.COMPLETED_FLAG)) != 1) {
                        _listView.expandGroup(i);
                        break;
                    }
                    i += 1;
                } while (cursor.moveToNext());
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(LOG_TAG, "Cursor index out of bounds:" + e.getMessage());
            }
        }
        try {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        if (id != LOADER_LEGS) {
            try {
                _listAdapter.setChildrenCursor(id, null);
            } catch (NullPointerException e) {
            }
        }

        _listAdapter.setGroupCursor(null);
    }

    @Override
    public void onRequestCompleted(int requestCode, WebResponse response) {
        switch (requestCode) {
            case ACCEPT_FILE_CODE:
                mAcceptButton.setVisibility(View.GONE);
                mRejectButton.setVisibility(View.GONE);
                _startFileButton.setVisibility(View.VISIBLE);

                if (response.getResponseCode() == WebResponse.ResponseCode.SUCCESS) {
                    Utils.showMessage(_activity, _activity.getString(R.string.accept_file_msg_) + mOrder.getFileNo());
                } else {
                    Utils.showMessage(_activity, _activity.getString(R.string.msg_not_sent));
                }
                break;
            case START_FILE_CODE:
                _startFileButton.setVisibility(View.GONE);
                mUpdateContainerNoButton.setVisibility(View.VISIBLE);
                mUpdateChassisNoButton.setVisibility(View.VISIBLE);
                mOrderStatus.setVisibility(View.VISIBLE);
                mUpdateContainerNoButton.requestFocus();
                if (response.getResponseCode() == WebResponse.ResponseCode.SUCCESS) {
                    Utils.showMessage(_activity, _activity.getString(R.string.start_file_msg) + mOrder.getFileNo());
                } else {
                    Utils.showMessage(_activity, _activity.getString(R.string.msg_not_sent));
                }
                _listAdapter.notifyDataSetChanged();
                break;

        }
    }

    private void deleteAssociateLegs(String orderId) {
        String condition = Leg.Columns.ORDER_ID + " = " + orderId;
        _activity.getContentResolver().delete(Leg.CONTENT_URI, condition, null);
    }

    public interface Callbacks {
        public void onOutboundFormClick(long legId, int fileNo);

    }
}
