package com.raildeliveryservices.burnrubber.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.data.UploadQ;
import com.raildeliveryservices.burnrubber.models.GpsLocation;
import com.raildeliveryservices.burnrubber.utils.Services;

import org.json.JSONObject;

public class UploadQueueAsyncTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = UploadQueueAsyncTask.class.getSimpleName();

    private Context _context;

    public UploadQueueAsyncTask(Context context) {
        _context = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        String url = params[0];
        String json = params[1];

        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonObject.accumulate(WebServiceConstants.FIELD_GPS, GpsLocation.getLocationString());

            ContentValues values = new ContentValues();
            values.put(UploadQ.Columns.SERVICE_URL, url);
            values.put(UploadQ.Columns.JSON_DATA, jsonObject.toString());
            values.put(UploadQ.Columns.DELETE_FLAG, false);

            _context.getContentResolver().insert(UploadQ.CONTENT_URI, values);
        } catch (Exception e) {
            Log.d(LOG_TAG, "doInBackground Exception: " + e.getMessage());
        }

        Services.startUploadService(_context);

        return null;
    }
}
