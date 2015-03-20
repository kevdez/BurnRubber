package com.raildeliveryservices.burnrubber.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.raildeliveryservices.burnrubber.data.UploadQ;
import com.raildeliveryservices.burnrubber.utils.WebPost;

public class UploadServiceAsyncTask extends AsyncTask<Void, Void, Void> {

	private static final String LOG_TAG = UploadServiceAsyncTask.class.getSimpleName();
	private Context _context;
	
	public UploadServiceAsyncTask(Context context) {
		_context = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		upload();
		return null;
	}

	private void upload() {
		Log.i(LOG_TAG, "Upload Service Started");
		Cursor cursor = getData();
		
		if (cursor.getCount() > 0) {
			do {
				long id = cursor.getLong(cursor.getColumnIndex(UploadQ.Columns._ID));
				String url = cursor.getString(cursor.getColumnIndex(UploadQ.Columns.SERVICE_URL));
				String json = cursor.getString(cursor.getColumnIndex(UploadQ.Columns.JSON_DATA));
				
				try {
					WebPost webPost = new WebPost(url);
					webPost.setJson(json);
					webPost.Post();

                    Log.d(LOG_TAG, "uploading id: "+id);
                    Log.d(LOG_TAG, "uploading to url: "+url);
                    Log.d(LOG_TAG, "uploading with json: "+json);



                    updateDeleteFlag(id);
				} catch (Exception e) {
					
				}
			} while (cursor.moveToNext());
			
			cursor.close();
			
			deleteRecords();
		}
	}
	
	private Cursor getData() {
		String[] projection = { UploadQ.Columns._ID,
								UploadQ.Columns.SERVICE_URL,
								UploadQ.Columns.JSON_DATA,
								UploadQ.Columns.DELETE_FLAG };
		String selection = UploadQ.Columns.DELETE_FLAG + " = 0";
		String sortOrder = UploadQ.Columns._ID;
		
		Cursor cursor = _context.getContentResolver().query(UploadQ.CONTENT_URI, projection, selection, null, sortOrder);
		cursor.moveToFirst();
		
		return cursor;
	}
	
	private void deleteRecords() {
		String selection = UploadQ.Columns.DELETE_FLAG + " = 1";
		_context.getContentResolver().delete(UploadQ.CONTENT_URI, selection, null);
	}
	
	private void updateDeleteFlag(long id) {
		Uri uri = Uri.withAppendedPath(UploadQ.CONTENT_URI, String.valueOf(id));
		
		ContentValues values = new ContentValues();
		values.put(UploadQ.Columns.DELETE_FLAG, true);
		
		_context.getContentResolver().update(uri, values, null, null);
	}
}
