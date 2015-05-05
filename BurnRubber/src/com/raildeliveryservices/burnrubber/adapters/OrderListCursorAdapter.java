package com.raildeliveryservices.burnrubber.adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.data.Leg;
import com.raildeliveryservices.burnrubber.data.Order;
import com.raildeliveryservices.burnrubber.utils.Utils;

import org.json.JSONObject;

public class OrderListCursorAdapter extends SimpleCursorAdapter {

    private String LOG_TAG = OrderListCursorAdapter.class.getSimpleName();
    private Context _context;
    private int _layout;
    private LayoutInflater _layoutInflater;
    private boolean _readOnly;

    public OrderListCursorAdapter(Context context, int layout, boolean readOnly) {
        super(context, layout, null, new String[]{Order.Columns.FILE_NO}, new int[]{R.id.fileNoText}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        _context = context;
        _layout = layout;
        _layoutInflater = LayoutInflater.from(context);
        _readOnly = readOnly;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return _layoutInflater.inflate(_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final long orderId = cursor.getLong(cursor.getColumnIndex(Order.Columns._ID));
        final int fileNo = cursor.getInt(cursor.getColumnIndex(Order.Columns.FILE_NO));

        final TextView fileNoText = (TextView) view.findViewById(com.raildeliveryservices.burnrubber.R.id.fileNoText);
        final TextView voyageNoText = (TextView) view.findViewById(R.id.voyageNoText);
        final TextView tripNoText = (TextView) view.findViewById(R.id.tripNoText);
        final TextView poNoText = (TextView) view.findViewById(R.id.poNoText);
        final TextView pickupNoText = (TextView) view.findViewById(R.id.pickupNoText);
        final TextView railNoText = (TextView) view.findViewById(R.id.railNoText);
        final TextView manifestNoText = (TextView) view.findViewById(R.id.manifestNoText);
        final TextView bookingNoText = (TextView) view.findViewById(R.id.bookingNoText);
        final TextView hazmatText = (TextView) view.findViewById(R.id.hazmatText);
        final TextView apptDateText = (TextView) view.findViewById(R.id.apptDateText);
        final TextView apptTimeText = (TextView) view.findViewById(R.id.apptTimeText);
        final TextView moveTypeText = (TextView) view.findViewById(R.id.moveTypeText);
        final Button acceptButton = (Button) view.findViewById(R.id.confirmButton);
        final Button rejectButton = (Button) view.findViewById(R.id.rejectButton);
        final boolean confirmedFlag = cursor.getInt(cursor.getColumnIndex(Order.Columns.CONFIRMED_FLAG)) == 1 ? true : false;
        final boolean hazmatFlag = cursor.getInt(cursor.getColumnIndex(Order.Columns.HAZMAT_FLAG)) == 1 ? true : false;

        fileNoText.setText(String.valueOf(fileNo));
        voyageNoText.setText("Leg No.: " + cursor.getString(cursor.getColumnIndex(Order.Columns.VOYAGE_NO)));
        tripNoText.setText("Trip No.: " + cursor.getString(cursor.getColumnIndex(Order.Columns.TRIP_NO)));
        poNoText.setText("PO No.: " + cursor.getString(cursor.getColumnIndex(Order.Columns.PO_NO)));
        pickupNoText.setText("Pickup No.: " + cursor.getString(cursor.getColumnIndex(Order.Columns.PICKUP_NO)));
        railNoText.setText("Rail No.: " + cursor.getString(cursor.getColumnIndex(Order.Columns.RAIL_NO)));
        manifestNoText.setText("Manifest No.: " + cursor.getString(cursor.getColumnIndex(Order.Columns.MANIFEST_NO)));
        bookingNoText.setText("Booking No.: " + cursor.getString(cursor.getColumnIndex(Order.Columns.BOOKING_NO)));

        if (hazmatFlag) {
            hazmatText.setVisibility(View.VISIBLE);
        } else {
            hazmatText.setVisibility(View.GONE);
        }

        apptDateText.setText(cursor.getString(cursor.getColumnIndex(Order.Columns.APPT_DATE_TIME)));
        apptTimeText.setText(cursor.getString(cursor.getColumnIndex(Order.Columns.APPT_TIME)));
        moveTypeText.setText(cursor.getString(cursor.getColumnIndex(Order.Columns.MOVE_TYPE)));


        if (confirmedFlag || _readOnly) {
            acceptButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
        } else {
            acceptButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);

            acceptButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues values = new ContentValues();
                    values.put(Order.Columns.CONFIRMED_FLAG, 1);
                    _context.getContentResolver().update(Uri.withAppendedPath(Order.CONTENT_URI, String.valueOf(orderId)), values, null, null);

                    JSONObject requestJson = new JSONObject();
                    try {
                        requestJson.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_context));
                        requestJson.accumulate(WebServiceConstants.FIELD_LABEL, "ACCEPT");
                        requestJson.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
                        requestJson.accumulate(WebServiceConstants.FIELD_FILE_NO, fileNoText.getText());
                        requestJson.accumulate(WebServiceConstants.FIELD_FORM_NAME, "CANNED");
                        requestJson.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, fileNoText.getText());
                        requestJson.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ClientDateFormat));

                        Utils.sendMessageToServer(_context, WebServiceConstants.URL_CREATE_MESSAGE, requestJson);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.getMessage());
                        Log.e(LOG_TAG, "URL: " + WebServiceConstants.URL_CREATE_MESSAGE);
                        Log.e(LOG_TAG, "JSON: " + requestJson.toString());
                    }
                }
            });

            rejectButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(_context);
                    dialogBuilder.setTitle(_context.getResources().getString(R.string.reject_order_dialog_title));
                    dialogBuilder.setMessage(String.format(_context.getResources().getString(R.string.reject_order_dialog_message), fileNoText.getText()));
                    dialogBuilder.setPositiveButton(_context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            _context.getContentResolver().delete(
                                    Uri.withAppendedPath(Order.CONTENT_URI, String.valueOf(orderId)), null, null);
                            deleteAssociateLegs(String.valueOf(orderId));
                            dialog.dismiss();

                            JSONObject requestJson = new JSONObject();
                            try {
                                requestJson.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_context));
                                requestJson.accumulate(WebServiceConstants.FIELD_LABEL, "REJECT");
                                requestJson.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
                                requestJson.accumulate(WebServiceConstants.FIELD_FILE_NO, fileNoText.getText());
                                requestJson.accumulate(WebServiceConstants.FIELD_FORM_NAME, "CANNED");
                                requestJson.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, fileNoText.getText());
                                requestJson.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ClientDateFormat));

                                Utils.sendMessageToServer(_context, WebServiceConstants.URL_CREATE_MESSAGE, requestJson);
                            } catch (Exception e) {
                                Log.e(LOG_TAG, e.getMessage());
                                Log.e(LOG_TAG, "URL: " + WebServiceConstants.URL_CREATE_MESSAGE);
                                Log.e(LOG_TAG, "JSON: " + requestJson.toString());
                            }
                        }
                    });
                    dialogBuilder.setNegativeButton(_context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialogBuilder.show();
                }
            });
        }
    }

    private void deleteAssociateLegs(String orderId) {
        String condition = Leg.Columns.ORDER_ID + " = " + orderId;
        _context.getContentResolver().delete(Leg.CONTENT_URI, condition, null);
    }
}
