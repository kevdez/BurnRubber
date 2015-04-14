package com.raildeliveryservices.burnrubber.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.models.GpsLocation;
import com.raildeliveryservices.burnrubber.utils.Utils;

import org.json.JSONObject;

public class LocationAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "LocationAsyncTask";
    private Context _context;

    public LocationAsyncTask(Context context) {
        _context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        saveLocation();
        return null;
    }

    private void saveLocation() {
        Log.i(TAG, "Location Service Started");

        try {
            if (!TextUtils.isEmpty(GpsLocation.getLocationString())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(_context));
                jsonObject.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
                jsonObject.accumulate(WebServiceConstants.FIELD_LABEL, "GPS");
                jsonObject.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ServerDateFormat));
                Utils.sendMessageToServer(_context, WebServiceConstants.URL_CREATE_MESSAGE, jsonObject);
            }
        } catch (Exception e) {
            Utils.sendDebugMessageToServer(_context, "LocationAsyncTask.saveLocation", e.getMessage());
        }
    }
}
