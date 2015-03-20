package com.raildeliveryservices.burnrubber.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.raildeliveryservices.burnrubber.data.Leg;
import com.raildeliveryservices.burnrubber.data.LegExtra;
import com.raildeliveryservices.burnrubber.data.LegOutbound;
import com.raildeliveryservices.burnrubber.data.Message;
import com.raildeliveryservices.burnrubber.data.Order;

public class DeleteOrderAsyncTask extends AsyncTask<Long, Void, Void> {
	
	private long _orderId;
	private Context _context;
	private ProgressDialog _progressDialog;
	
	public DeleteOrderAsyncTask(Context context) {
		_context = context;
	}
	
	@Override
	protected Void doInBackground(Long... params) {
		
		_orderId = params[0];
		
		_context.getContentResolver().delete(Uri.withAppendedPath(Order.CONTENT_URI, String.valueOf(_orderId)), null, null);
		_context.getContentResolver().delete(Message.CONTENT_URI, Message.Columns.ORDER_ID + " = " + _orderId, null);
		
		Cursor cursor = _context.getContentResolver().query(Leg.CONTENT_URI, new String[] { Leg.Columns._ID }, Leg.Columns.ORDER_ID + " = " + _orderId, null, null);
		cursor.moveToFirst();
		
		do {
			_context.getContentResolver().delete(LegExtra.CONTENT_URI, LegExtra.Columns.LEG_ID + " = " + cursor.getLong(cursor.getColumnIndex(Leg.Columns._ID)), null);
			_context.getContentResolver().delete(LegOutbound.CONTENT_URI, LegOutbound.Columns.LEG_ID + " = " + cursor.getLong(cursor.getColumnIndex(Leg.Columns._ID)), null);
		} while (cursor.moveToNext());
		
		cursor.close();
		
		_context.getContentResolver().delete(Leg.CONTENT_URI, Leg.Columns.ORDER_ID + " = " + _orderId, null);
		
		return null;
	}
	
	@Override
	protected void onPreExecute() {
		_progressDialog = ProgressDialog.show(_context,
				"Deleting Trip", "Trip history delete in progress...");
	}
	
	@Override
	protected void onPostExecute(Void result) {
		_progressDialog.dismiss();
	}
}