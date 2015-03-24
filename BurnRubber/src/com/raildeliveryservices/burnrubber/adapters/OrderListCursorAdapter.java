package com.raildeliveryservices.burnrubber.adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.data.Order;

public class OrderListCursorAdapter extends SimpleCursorAdapter {

	private Context _context;
	private int _layout;
	private LayoutInflater _layoutInflater;
	private boolean _readOnly;
	
	public OrderListCursorAdapter(Context context, int layout, boolean readOnly) {
		super(context, layout, null, new String[] { Order.Columns.FILE_NO }, new int[] { R.id.fileNoText }, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
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
		final TextView hazmatText = (TextView) view.findViewById(R.id.hazmatText);
		final TextView apptDateText = (TextView) view.findViewById(R.id.apptDateText);
        final TextView apptTimeText = (TextView) view.findViewById(R.id.apptTimeText);
		final TextView moveTypeText = (TextView) view.findViewById(R.id.moveTypeText);
		final Button confirmButton = (Button) view.findViewById(R.id.confirmButton);
		final Button rejectButton = (Button) view.findViewById(R.id.rejectButton);
		final boolean confirmedFlag = cursor.getInt(cursor.getColumnIndex(Order.Columns.CONFIRMED_FLAG)) == 1 ? true : false;
		final boolean hazmatFlag = cursor.getInt(cursor.getColumnIndex(Order.Columns.HAZMAT_FLAG)) == 1 ? true : false;
		
		fileNoText.setText(String.valueOf(fileNo));
		voyageNoText.setText(cursor.getString(cursor.getColumnIndex(Order.Columns.VOYAGE_NO)));
		
		if (hazmatFlag)
		{
			hazmatText.setVisibility(View.VISIBLE);
		} else {
			hazmatText.setVisibility(View.GONE);
		}
		
		apptDateText.setText(cursor.getString(cursor.getColumnIndex(Order.Columns.APPT_DATE_TIME)));
        apptTimeText.setText(cursor.getString(cursor.getColumnIndex(Order.Columns.APPT_TIME)));
		moveTypeText.setText(cursor.getString(cursor.getColumnIndex(Order.Columns.MOVE_TYPE)));
		
		confirmButton.setTag(orderId);
		rejectButton.setTag(orderId);
		
		if (confirmedFlag || _readOnly) {
			confirmButton.setVisibility(View.GONE);
			rejectButton.setVisibility(View.GONE);
		} else {
			confirmButton.setVisibility(View.VISIBLE);
			rejectButton.setVisibility(View.VISIBLE);
			
			confirmButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentValues values = new ContentValues();
					values.put(Order.Columns.CONFIRMED_FLAG, 1);
					_context.getContentResolver().update(Uri.withAppendedPath(Order.CONTENT_URI, String.valueOf(v.getTag())), values, null, null);
					
					/*
					try {
						SharedPreferences settings = _context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
						
						JSONObject jsonObject = new JSONObject();
						jsonObject.accumulate(Constants.WEB_SERVICE_FIELD_FILE_NO, fileNo);
						jsonObject.accumulate(Constants.WEB_SERVICE_FIELD_CONFIRMATION_TYPE, "Accepted");
						jsonObject.accumulate(Constants.WEB_SERVICE_FIELD_LAST_UPDATE_USER_NAME, settings.getString(Constants.SETTINGS_USER_NAME, ""));
						
						UploadQueueAsyncTask uploadAsyncTask = new UploadQueueAsyncTask(_context);
						uploadAsyncTask.execute(new String[] { Constants.WEB_SERVICE_CONFIRM_REJECT_ORDER_URL, jsonObject.toString() });
					} catch (JSONException e) {
					}
					*/
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
									Uri.withAppendedPath(Order.CONTENT_URI, String.valueOf(v.getTag())),
									null,
									null);
							
							/*
							try {
								SharedPreferences settings = _context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
								
								JSONObject jsonObject = new JSONObject();
								jsonObject.accumulate(Constants.WEB_SERVICE_FIELD_FILE_NO, fileNo);
								jsonObject.accumulate(Constants.WEB_SERVICE_FIELD_CONFIRMATION_TYPE, "Rejected");
								jsonObject.accumulate(Constants.WEB_SERVICE_FIELD_LAST_UPDATE_USER_NAME, settings.getString(Constants.SETTINGS_USER_NAME, ""));
								
								UploadQueueAsyncTask uploadAsyncTask = new UploadQueueAsyncTask(_context);
								uploadAsyncTask.execute(new String[] { Constants.WEB_SERVICE_CONFIRM_REJECT_ORDER_URL, jsonObject.toString() });
							} catch (JSONException e) {
							}
							*/
							
							dialog.dismiss();
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
}
