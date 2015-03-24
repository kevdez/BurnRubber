package com.raildeliveryservices.burnrubber.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.data.Message;
import com.raildeliveryservices.burnrubber.models.SoundPlayer;
import com.raildeliveryservices.burnrubber.utils.Utils;
import com.raildeliveryservices.burnrubber.utils.WebPost;

import org.json.JSONArray;
import org.json.JSONObject;

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

			saveMessages(responseJson.getJSONArray(WebServiceConstants.OBJECT_MESSAGES));
			
		} catch (Exception e) {
			Utils.sendDebugMessageToServer(_context, "DownloadMessagesServiceAsyncTask.downloadMessages", e.getMessage());
			Log.e(LOG_TAG, e.getMessage());
		}
	}
	
	/*
	private long orderExists(int fileNo) {
		Uri uri = Order.CONTENT_URI;
		String[] projection = { Order.Columns._ID, Order.Columns.FILE_NO };
		String selection = Order.Columns.FILE_NO + " = " + String.valueOf(fileNo);
		
		Cursor cursor = getContentResolver().query(uri, projection, selection, null, null);
			
		if (cursor.getCount() > 0) {
			return cursor.getLong(cursor.getColumnIndex(Order.Columns._ID));
		} else {
			return 0;
		}
	}
	*/
	
	private void saveMessages(JSONArray messageArray) {
		
		boolean hasMessage = false;

        for (int i = 0; i < messageArray.length(); i++) {
			try {
				JSONObject messageObject = messageArray.getJSONObject(i);
				
				if ("MSG".equals(messageObject.getString(WebServiceConstants.FIELD_LABEL))) {
					hasMessage = true;
				
					ContentValues values = new ContentValues();
					values.put(Message.Columns.DRIVER_NO, messageObject.getInt(WebServiceConstants.FIELD_DRIVER_NO));
					values.put(Message.Columns.MESSAGE_TYPE, "Dispatch");
					values.put(Message.Columns.MESSAGE_TEXT, messageObject.getString(WebServiceConstants.FIELD_MESSAGE_TEXT));
					
					String tempDate = messageObject.getString(WebServiceConstants.FIELD_CLIENT_DATETIME).replace("T", " ");
					values.put(Message.Columns.CREATED_DATE_TIME, Utils.convertTabletDateTime(tempDate));
					
					_context.getContentResolver().insert(Message.CONTENT_URI, values);
				}
				
				_settings.edit().putLong(Constants.SETTINGS_LAST_DOWNLOADED_MESSAGE_ID + "-" + Utils.getDriverNo(_context), messageObject.getLong(WebServiceConstants.FIELD_MESSAGE_ID)).commit();
			} catch (Exception e) {
				Utils.sendDebugMessageToServer(_context, "DownloadMessagesServiceAsyncTask.saveMessages", e.getMessage());
				Log.e(LOG_TAG, e.getMessage());
			}
		}

		if (hasMessage) {
			//Utils.sendNotification(this, Constants.NOTIFICATION_MESSAGES, getString(R.string.message_notification_title), getString(R.string.message_notification_message), MessageActivity.class);
			Utils.setMessageAlertFlag(_context, Utils.getDriverNo(_context), true);
			SoundPlayer.playSound(_context, com.raildeliveryservices.burnrubber.R.raw.notification);
		}
	}
}
