package com.raildeliveryservices.burnrubber.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.data.Form;
import com.raildeliveryservices.burnrubber.utils.WebPost;

import org.json.JSONArray;
import org.json.JSONObject;

public class DownloadFormServiceAsyncTask extends AsyncTask<Void, Void, Void> {

	private Context _context;
	
	public DownloadFormServiceAsyncTask(Context context) {
		_context = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		downloadForms();
		return null;
	}

	private void downloadForms() {
		try {
			WebPost webPost = new WebPost(WebServiceConstants.URL_GET_FORMS);
			JSONObject response = webPost.Post();
			saveForms(response.getJSONArray(WebServiceConstants.OBJECT_FORMS));
		} catch (Exception e) {
			Log.e("DownloadFormsService", e.getMessage());
		}
	}
	
	private void saveForms(JSONArray formsArray) {
				
		_context.getContentResolver().delete(Form.CONTENT_URI, null, null);
		
		for (int i = 0; i < formsArray.length(); i++) {
			try {
				JSONObject formObject = formsArray.getJSONObject(i);
				
				ContentValues values = new ContentValues();
				values.put(Form.Columns.FORM_NAME, formObject.getString(WebServiceConstants.FIELD_FORM_NAME));
				values.put(Form.Columns.LABEL, formObject.getString(WebServiceConstants.FIELD_LABEL));
				values.put(Form.Columns.FILL_IN, formObject.getInt(WebServiceConstants.FIELD_FILL_IN));
				values.put(Form.Columns.FORM_TYPE, formObject.getString(WebServiceConstants.FIELD_FORM_TYPE));
				values.put(Form.Columns.MUST_FILL_FLAG, formObject.getBoolean(WebServiceConstants.FIELD_MUST_FILL_FLAG) == true ? 1 : 0);
				values.put(Form.Columns.DRIVER_FLAG, formObject.getBoolean(WebServiceConstants.FIELD_DRIVER_FLAG) == true ? 1 : 0);
				
				_context.getContentResolver().insert(Form.CONTENT_URI, values);
			} catch (Exception e) {
				Log.e("DownloadFormsService", e.getMessage());
			}
		}
	}
}
