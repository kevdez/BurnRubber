package com.raildeliveryservices.burnrubber.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.raildeliveryservices.burnrubber.LoginActivity;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.utils.RuntimeSetting;
import com.raildeliveryservices.burnrubber.utils.Utils;
import com.raildeliveryservices.burnrubber.utils.WebPost;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nghia on 04/15/2015.
 */
public class DownloadSettingsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private static final String TAG = DownloadSettingsAsyncTask.class.getSimpleName();


    public DownloadSettingsAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "doInBackground");
        try {
            WebPost webPost = new WebPost(WebServiceConstants.URL_GET_SETTING);
            JSONObject response = webPost.Post();
            updateSettingsFromServer(response);
            Log.d(TAG, "DownloadSettingFromServer completed.");
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void Void){
        mContext.startActivity(new Intent(mContext, LoginActivity.class));
    }

    private void updateSettingsFromServer(JSONObject response) throws JSONException {
        RuntimeSetting.downloadMessageInterval  = Integer.parseInt(response.getString(WebServiceConstants.FIELD_DOWNLOAD_MESSAGE_INTERVAL));
        RuntimeSetting.downloadOrderInterval = Integer.parseInt(response.getString(WebServiceConstants.FIELD_DOWNLOAD_ORDER_INTERVAL));
        RuntimeSetting.uploadServiceInterval = Integer.parseInt(response.getString(WebServiceConstants.FIELD_UPLOAD_SERVICE_INTERVAL));
        RuntimeSetting.locationServiceInterval = Integer.parseInt(response.getString(WebServiceConstants.FIELD_LOCATION_SERVICE_INTERVAL));
        RuntimeSetting.locationUpdateInterval = Integer.parseInt(response.getString(WebServiceConstants.FIELD_LOCATION_UPDATE_INTERVAL));
        RuntimeSetting.fastestLocationUpdateInterval = Integer.parseInt(response.getString(WebServiceConstants.FIELD_FASTEST_LOCATION_UPDATE_INTERVAL));
        RuntimeSetting.sendGpsWhenOffline = Boolean.parseBoolean(response.getString(WebServiceConstants.FIELD_SEND_GPS_MESSAGE_WHEN_OFFLINE));

        Utils.saveRuntimeSetting(mContext);

        Log.d(TAG, "downloadMessageInterval = " + RuntimeSetting.downloadMessageInterval);
        Log.d(TAG, "downloadOrderInterval = " +  RuntimeSetting.downloadOrderInterval);
        Log.d(TAG, "uploadServiceInterval = " +  RuntimeSetting.uploadServiceInterval);
        Log.d(TAG, "locationServiceInterval = " + RuntimeSetting.locationServiceInterval);
        Log.d(TAG, "locationUpdateInterval = " +  RuntimeSetting.locationUpdateInterval);
        Log.d(TAG, "fastestLocationUpdateInterval = " +  RuntimeSetting.fastestLocationUpdateInterval);
        Log.d(TAG, "sendGpsMessageWhenOffline = " + RuntimeSetting.sendGpsWhenOffline);
    }

}
