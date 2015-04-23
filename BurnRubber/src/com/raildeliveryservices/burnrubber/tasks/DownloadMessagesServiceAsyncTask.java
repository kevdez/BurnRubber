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
import com.raildeliveryservices.burnrubber.data.LegExtra;
import com.raildeliveryservices.burnrubber.data.LegOutbound;
import com.raildeliveryservices.burnrubber.data.Message;
import com.raildeliveryservices.burnrubber.data.Order;
import com.raildeliveryservices.burnrubber.models.SoundPlayer;
import com.raildeliveryservices.burnrubber.utils.RuntimeSetting;
import com.raildeliveryservices.burnrubber.utils.Utils;
import com.raildeliveryservices.burnrubber.utils.WebPost;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class DownloadMessagesServiceAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = DownloadMessagesServiceAsyncTask.class.getSimpleName();
    private Context _context;
    private SharedPreferences _settings;

    public DownloadMessagesServiceAsyncTask(Context context) {
        _context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        _settings = _context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        downloadMessages();
        Log.d(LOG_TAG, "doInBackGround");
        return null;
    }

    private void downloadMessages() {
        long lastMessageId = _settings.getLong(Constants.SETTINGS_LAST_DOWNLOADED_MESSAGE_ID + "-" + Utils.getDriverNo(_context), 0);
        Log.i(LOG_TAG, Constants.SETTINGS_LAST_DOWNLOADED_MESSAGE_ID + "-" + Utils.getDriverNo(_context) + " = " + String.valueOf(lastMessageId));

        try {
            JSONObject requestJson = new JSONObject();
            requestJson.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_context));
            requestJson.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "O");
            requestJson.accumulate(WebServiceConstants.FIELD_MESSAGE_START_ID, lastMessageId);

            WebPost webPost = new WebPost(WebServiceConstants.URL_GET_MESSAGES);
            webPost.setJson(requestJson.toString());
            JSONObject responseJson = webPost.Post();
            Log.d(LOG_TAG, responseJson.toString());
            saveMessages(responseJson.getJSONArray(WebServiceConstants.OBJECT_MESSAGES));

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void saveMessages(JSONArray messageArray) {

        boolean hasMessage = false;
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        for (int i = 0; i < messageArray.length(); i++) {
            try {
                JSONObject messageObject = messageArray.getJSONObject(i);

                if ("DELETE".equals(messageObject.get(WebServiceConstants.FIELD_LABEL))) {
                    Long order_id = orderExists(messageObject.getInt(WebServiceConstants.FIELD_MESSAGE_TEXT));
                    Log.d(LOG_TAG, "In saveMessages, deleting order ID: " + order_id + " and file No.: " + messageObject.getString(WebServiceConstants.FIELD_MESSAGE_TEXT));
                    deleteOrder(order_id);
                } else if ("MSG".equals(messageObject.getString(WebServiceConstants.FIELD_LABEL))) {
                    hasMessage = true;

                    ContentValues values = new ContentValues();
                    values.put(Message.Columns.DRIVER_NO, messageObject.getInt(WebServiceConstants.FIELD_DRIVER_NO));
                    values.put(Message.Columns.MESSAGE_TYPE, "Dispatch");
                    values.put(Message.Columns.MESSAGE_TEXT, messageObject.getString(WebServiceConstants.FIELD_MESSAGE_TEXT));

                    Date date = Utils.toDate(messageObject.getString(WebServiceConstants.FIELD_CLIENT_DATETIME), "yyyy-MM-dd'T'HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.SECOND, RuntimeSetting.syncTimeInSeconds);
                    //Add 1 second for messages to be sorted in the right order.
                    now.add(Calendar.SECOND, 1);
                    Date savedDate = calendar.getTimeInMillis() > now.getTimeInMillis() ? now.getTime() : calendar.getTime();
                    values.put(Message.Columns.CREATED_DATE_TIME, Constants.SQLiteDateFormat.format(savedDate));

                    _context.getContentResolver().insert(Message.CONTENT_URI, values);
                }

                _settings.edit().putLong(Constants.SETTINGS_LAST_DOWNLOADED_MESSAGE_ID + "-" + Utils.getDriverNo(_context), messageObject.getLong(WebServiceConstants.FIELD_MESSAGE_ID)).commit();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        if (hasMessage) {
            Utils.setMessageAlertFlag(_context, Utils.getDriverNo(_context), true);
            SoundPlayer.playSound(_context, com.raildeliveryservices.burnrubber.R.raw.notification);
        }
    }

    private long orderExists(int fileNo) {
        Uri uri = Order.CONTENT_URI;
        String[] projection = {Order.Columns._ID, Order.Columns.FILE_NO};
        String selection = Order.Columns.FILE_NO + " = " + String.valueOf(fileNo);

        Cursor cursor = _context.getContentResolver().query(uri, projection, selection, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return cursor.getLong(cursor.getColumnIndex(Order.Columns._ID));
        } else {
            return 0;
        }
    }

    private void deleteOrder(Long param) {

        Long _orderId = param;

        _context.getContentResolver().delete(Uri.withAppendedPath(Order.CONTENT_URI, String.valueOf(_orderId)), null, null);
        _context.getContentResolver().delete(Message.CONTENT_URI, Message.Columns.ORDER_ID + " = " + _orderId, null);

        Cursor cursor = _context.getContentResolver().query(Leg.CONTENT_URI, new String[]{Leg.Columns._ID}, Leg.Columns.ORDER_ID + " = " + _orderId, null, null);
        cursor.moveToFirst();

        do {
            _context.getContentResolver().delete(LegExtra.CONTENT_URI, LegExtra.Columns.LEG_ID + " = " + cursor.getLong(cursor.getColumnIndex(Leg.Columns._ID)), null);
            _context.getContentResolver().delete(LegOutbound.CONTENT_URI, LegOutbound.Columns.LEG_ID + " = " + cursor.getLong(cursor.getColumnIndex(Leg.Columns._ID)), null);
        } while (cursor.moveToNext());

        cursor.close();

        _context.getContentResolver().delete(Leg.CONTENT_URI, Leg.Columns.ORDER_ID + " = " + _orderId, null);

    }
}
