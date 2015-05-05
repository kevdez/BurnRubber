package com.raildeliveryservices.burnrubber.tasks;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.raildeliveryservices.burnrubber.R;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.data.UploadQ;
import com.raildeliveryservices.burnrubber.data.WebResponse;
import com.raildeliveryservices.burnrubber.models.GpsLocation;
import com.raildeliveryservices.burnrubber.utils.WebPost;

import org.json.JSONObject;


/**
 * Created by nghia on 05/01/2015.
 */
public class SendRequestAsyncTask extends AsyncTask<String, Void, WebResponse> {
    private final String TAG = this.getClass().getSimpleName();
    private String mUrl;
    private String mJsonData;
    private Context mContext;
    private ProgressDialog progressDialog;
    private int mRequestCode;
    private OnRequestCompleted mRequestListener;

    public SendRequestAsyncTask(Context context, int requestCode, OnRequestCompleted requestListener) {
        mContext = context;
        mRequestCode = requestCode;
        mRequestListener = requestListener;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(mContext, "Sending Request...", "Your request is being sent to the server.");
    }

    @Override
    protected WebResponse doInBackground(String... params) {
        //The first param is request URL, the second one is request Json data
        mUrl = params[0];
        mJsonData = params[1];
        WebResponse webResponse = new WebResponse();
        webResponse.setRequestedURL(mUrl);
        try {
            WebPost webPost = new WebPost(mUrl);
            webPost.setJson(mJsonData);
            JSONObject response = webPost.Post();
            webResponse.setResponseCode(WebResponse.ResponseCode.SUCCESS);
            webResponse.setMessage(response.toString());
        } catch (Exception ex) {
            webResponse.setResponseCode(WebResponse.ResponseCode.FAIL);
            webResponse.setMessage(mContext.getString(R.string.fail_to_send_request));
            addToRequestQueue();
            Log.d(TAG, "Fail to send request to " + mUrl);
        }
        return webResponse;
    }

    private void addToRequestQueue() {
        try {
            JSONObject jsonObject = new JSONObject(mJsonData);
            jsonObject.accumulate(WebServiceConstants.FIELD_GPS, GpsLocation.getLocationString());

            ContentValues values = new ContentValues();
            values.put(UploadQ.Columns.SERVICE_URL, mUrl);
            values.put(UploadQ.Columns.JSON_DATA, jsonObject.toString());
            values.put(UploadQ.Columns.DELETE_FLAG, false);

            mContext.getContentResolver().insert(UploadQ.CONTENT_URI, values);
        } catch (Exception ex) {
            Log.d(TAG, "Failed to add request to queue");
        }
    }

    @Override
    protected void onPostExecute(WebResponse webResponse) {
        progressDialog.dismiss();
        mRequestListener.onRequestCompleted(mRequestCode, webResponse);
    }

    public static interface OnRequestCompleted {
        void onRequestCompleted(int requestCode, WebResponse response);
    }
}
