package com.raildeliveryservices.burnrubber.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.data.Leg;
import com.raildeliveryservices.burnrubber.data.Order;
import com.raildeliveryservices.burnrubber.utils.Utils;
import com.raildeliveryservices.burnrubber.utils.WebPost;

import org.json.JSONArray;
import org.json.JSONObject;

public class DownloadOrdersServiceAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = DownloadOrdersServiceAsyncTask.class.getSimpleName();
    private Context _context;
    private SharedPreferences _settings;

    public DownloadOrdersServiceAsyncTask(Context context) {
        _context = context;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        _settings = _context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        downloadOrders();
        Log.d(LOG_TAG, "doInBackGround");
        return null;
    }

    private void downloadOrders() {
        String lastUpdateDateTime = _settings.getString(Constants.SETTINGS_LAST_REQUEST_DATE_TIME_ORDERS + "-" + Utils.getDriverNo(_context), "2000-01-01 00:00:00.000");
        try {

            JSONObject requestJson = new JSONObject();
            requestJson.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_context));
            requestJson.accumulate(WebServiceConstants.FIELD_LAST_UPDATE_DATE_TIME_ORDERS, lastUpdateDateTime);

            WebPost webPost = new WebPost(WebServiceConstants.URL_GET_ORDERS);
            webPost.setJson(requestJson.toString());
            JSONObject responseJson = webPost.Post();
            saveOrders(responseJson.getJSONArray("Orders"));

            String lastRequestUTCTime = responseJson.getString(WebServiceConstants.FIELD_LAST_REQUEST_DATE_TIME_ORDERS);
            _settings.edit().putString(Constants.SETTINGS_LAST_REQUEST_DATE_TIME_ORDERS + "-" + Utils.getDriverNo(_context), lastRequestUTCTime).commit();
            Log.d(LOG_TAG, "requestTime: " + lastUpdateDateTime + responseJson.toString());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void saveOrders(JSONArray orderArray) {
        Uri uri = Order.CONTENT_URI;
        for (int i = 0; i < orderArray.length(); i++) {

            try {
                JSONObject orderObject = orderArray.getJSONObject(i);

                ContentValues values = new ContentValues();
                values.put(Order.Columns.FILE_NO, orderObject.getInt("FileNo"));
                values.put(Order.Columns.PARENT_FILE_NO, orderObject.getInt("ParentFileNo"));
                values.put(Order.Columns.DRIVER_NO, orderObject.getInt("DriverNo"));
                values.put(Order.Columns.VOYAGE_NO, orderObject.getString("VoyageNo"));
                values.put(Order.Columns.TRIP_NO, orderObject.getString("TripNo"));
                values.put(Order.Columns.PO_NO, orderObject.getString("PONo"));
                values.put(Order.Columns.PICKUP_NO, orderObject.getString("PickupNo"));
                values.put(Order.Columns.RAIL_NO, orderObject.getString("RailNo"));
                values.put(Order.Columns.MANIFEST_NO, orderObject.getString("ManifestNo"));
                values.put(Order.Columns.BOOKING_NO, orderObject.getString("BookingNo"));

                values.put(Order.Columns.HAZMAT_FLAG, orderObject.getBoolean("HazmatFlag"));

                // show only the date portion
                String tempDate = orderObject.getString("AppointmentDateTime").substring(0, 10);
                values.put(Order.Columns.APPT_DATE_TIME, tempDate);

                values.put(Order.Columns.APPT_TIME, orderObject.getString("AppointmentTime"));
                values.put(Order.Columns.MOVE_TYPE, orderObject.getString("MoveType"));
                values.put(Order.Columns.CONTAINER_NO, orderObject.getString("ContainerNo"));
                values.put(Order.Columns.CHASSIS_NO, orderObject.getString("ChassisNo"));
                values.put(Order.Columns.LUMPER_FLAG, orderObject.getBoolean("LumperFlag") ? 1 : 0);
                values.put(Order.Columns.SCALE_FLAG, orderObject.getBoolean("ScaleFlag") ? 1 : 0);
                values.put(Order.Columns.WEIGHT_FLAG, orderObject.getBoolean("WeightFlag") ? 1 : 0);
                values.put(Order.Columns.COMMENTS, orderObject.getString("Comments"));
                values.put(Order.Columns.CONFIRMED_FLAG, 0);
                values.put(Order.Columns.STARTED_FLAG, 0);
                values.put(Order.Columns.COMPLETED_FLAG, 0);

                int fileNo = orderObject.getInt("FileNo");
                long orderId = orderExistedButNotCompleted(fileNo);

                if (orderId > 0) {
                    uri = Uri.withAppendedPath(uri, String.valueOf(orderId));
                    //Delete all the associate legs of the order before saving the new legs.
                    deleteLegsOfOrder(orderId);
                    _context.getContentResolver().update(uri, values, null, null);
                } else {
                    Uri returnUri = _context.getContentResolver().insert(uri, values);
                    orderId = Long.parseLong(returnUri.getLastPathSegment());
                }

                saveLegs(orderId, fileNo, orderObject.getJSONArray("Legs"));

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }

    private void deleteLegsOfOrder(long orderId) {
        String[] projections = {Leg.Columns._ID};
        String selection = Leg.Columns.ORDER_ID + " = " + orderId;
        Cursor cursor = _context.getContentResolver().query(Leg.CONTENT_URI, projections, selection, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int legId = cursor.getInt(cursor.getColumnIndex(Leg.Columns._ID));
                Uri deleteLegUri = Uri.withAppendedPath(Leg.CONTENT_URI, String.valueOf(legId));
                _context.getContentResolver().delete(deleteLegUri, null, null);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    private void saveLegs(long orderId, int fileNo, JSONArray legArray) {
        Uri uri = Leg.CONTENT_URI;
        try {
            for (int i = 0; i < legArray.length(); i++) {
                JSONObject legObject = legArray.getJSONObject(i);

                int legNo = legObject.getInt("LegNo");

                ContentValues values = new ContentValues();
                values.put(Leg.Columns.FILE_NO, fileNo);
                values.put(Leg.Columns.LEG_NO, legNo);
                values.put(Leg.Columns.PARENT_LEG_NO, legObject.getInt("ParentLegNo"));
                values.put(Leg.Columns.COMPANY_NAME_FROM, legObject.getString("CompanyNameFrom"));
                values.put(Leg.Columns.ADDRESS_FROM, legObject.getString("AddressFrom"));
                values.put(Leg.Columns.CITY_FROM, legObject.getString("CityFrom"));
                values.put(Leg.Columns.STATE_FROM, legObject.getString("StateFrom"));
                values.put(Leg.Columns.ZIP_CODE_FROM, legObject.getString("ZipCodeFrom"));
                values.put(Leg.Columns.COMPANY_NAME_TO, legObject.getString("CompanyNameTo"));
                values.put(Leg.Columns.ADDRESS_TO, legObject.getString("AddressTo"));
                values.put(Leg.Columns.CITY_TO, legObject.getString("CityTo"));
                values.put(Leg.Columns.STATE_TO, legObject.getString("StateTo"));
                values.put(Leg.Columns.ZIP_CODE_TO, legObject.getString("ZipCodeTo"));
                values.put(Leg.Columns.COUNT_FLAG, legObject.getBoolean("CountFlag") ? 1 : 0);
                values.put(Leg.Columns.OUTBOUND_FLAG, legObject.getBoolean("OutboundFlag") ? 1 : 0);
                values.put(Leg.Columns.WEIGHT_FLAG, legObject.getBoolean("WeightFlag") ? 1 : 0);
                values.put(Leg.Columns.YARD_LOCATION, legObject.getString("YardLocation"));
                values.put(Leg.Columns.COMMODITY, legObject.getString("Commodity"));
                values.put(Leg.Columns.BOL, legObject.getString("BOL"));
                values.put(Leg.Columns.ORDER_ID, orderId);
                values.put(Leg.Columns.COMPLETED_FLAG, 0);
                _context.getContentResolver().insert(uri, values);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private long orderExistedButNotCompleted(int fileNo) {
        Uri uri = Order.CONTENT_URI;
        String[] projection = {Order.Columns._ID, Order.Columns.FILE_NO, Order.Columns.COMPLETED_FLAG};
        String selection = Order.Columns.FILE_NO + " = " + String.valueOf(fileNo);
        selection += " AND " + Order.Columns.COMPLETED_FLAG + " != 1";//Completed order.

        Cursor cursor = _context.getContentResolver().query(uri, projection, selection, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getLong(cursor.getColumnIndex(Order.Columns._ID));
        } else {
            return 0;
        }
    }

}
