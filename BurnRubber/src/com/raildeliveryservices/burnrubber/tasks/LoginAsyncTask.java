package com.raildeliveryservices.burnrubber.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import com.raildeliveryservices.burnrubber.OrderActivity;
import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.models.AuthenticationResponse;
import com.raildeliveryservices.burnrubber.utils.Services;
import com.raildeliveryservices.burnrubber.utils.Utils;
import com.raildeliveryservices.burnrubber.utils.WebPost;

import org.json.JSONObject;

public class LoginAsyncTask extends AsyncTask<String, Void, AuthenticationResponse> {

	private Context _context;
	private ProgressDialog _progressDialog;
	private String _driverNo;
	
	public LoginAsyncTask(Context context) {
		_context = context;
	}
	
	@Override
	protected void onPreExecute() {
		_progressDialog = ProgressDialog.show(_context,
		_context.getResources().getString(R.string.auth_progress_title),
		_context.getResources().getString(R.string.auth_progress_message));
	}
	
	@Override
	protected AuthenticationResponse doInBackground(String... params) {
		
		AuthenticationResponse loginResponse = new AuthenticationResponse();
		_driverNo = params[0];
		String password = params[1];
		
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate(WebServiceConstants.FIELD_DRIVER_NO, _driverNo);
			jsonObject.accumulate(WebServiceConstants.FIELD_PASSWORD, password);
			
			WebPost webPost = new WebPost(WebServiceConstants.URL_LOGIN);
			webPost.setJson(jsonObject.toString());
			JSONObject returnJsonObject = webPost.Post();
			
			loginResponse.authentic = returnJsonObject.getBoolean(WebServiceConstants.FIELD_AUTHENTIC);
			
			if (returnJsonObject.has(WebServiceConstants.OBJECT_RESPONSE_MESSAGE)) {
				loginResponse.message = returnJsonObject.getString("ResponseMessage");
			}
			
			return loginResponse;
		}
		catch (Exception e) {
			Utils.sendDebugMessageToServer(_context, "LoginAsyncTask.doInBackground", e.getMessage());
			loginResponse.authentic = false;
			loginResponse.message = "An unexpected error occured.  Please contact to administrator.";
			return loginResponse;
		}
	}
	
	@Override
	protected void onPostExecute(AuthenticationResponse result) {		
		_progressDialog.dismiss();
		
		if (result.authentic) {
			Utils.setDriverNo(_context, _driverNo);
			Utils.setUserLoggedIn(_context, true);

			_context.startActivity(new Intent(_context, OrderActivity.class));
		} else {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_context);
			alertBuilder.setTitle(_context.getResources().getString(R.string.auth_error_dialog_title));
			alertBuilder.setMessage(result.message);
			alertBuilder.setPositiveButton(_context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alertBuilder.create().show();
		}
	}
}