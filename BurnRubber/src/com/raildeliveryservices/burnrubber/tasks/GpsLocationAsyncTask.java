package com.raildeliveryservices.burnrubber.tasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.raildeliveryservices.burnrubber.models.GpsLocation;
import com.raildeliveryservices.burnrubber.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GpsLocationAsyncTask extends AsyncTask<Location, Void, String> {

    private static final String TAG = GpsLocationAsyncTask.class.getSimpleName();
    private Context _context;
    private Location _location;

    public GpsLocationAsyncTask(Context context) {
        _context = context;
    }

    private static JSONObject getLocationInfo(double lat, double lng) throws IOException, JSONException {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        int b;
        while ((b = stream.read()) != -1) {
            stringBuilder.append((char) b);
        }

        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
        return jsonObject;
    }

    @Override
    protected String doInBackground(Location... params) {

        Geocoder geocoder = new Geocoder(_context);
        _location = params[0];
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(_location.getLatitude(), _location.getLongitude(), 1);
        } catch (Exception e) {
            //TODO: Check for exception when getting GPS address. Max accepted msg length 4000.
            String exceptionMsg = e.getMessage();
            if (exceptionMsg.length() > 3900) {
                exceptionMsg = exceptionMsg.substring(0, 3900);
            }
            Utils.sendDebugMessageToServer(_context, "GpsLocationAsyncTask.doInBackground", exceptionMsg);
            Log.d(TAG, exceptionMsg, e);
            return googleMapsAPIRequest();
        }

        if (addresses != null & addresses.size() > 0) {
            Address address = addresses.get(0);

            String addressText = String.format(
                    "%s, %s, %s, %s",
                    address.getMaxAddressLineIndex() > 0 ?
                            address.getAddressLine(0) : "",
                    address.getLocality(),
                    address.getAdminArea(),
                    address.getPostalCode());
            return addressText;
        } else {
            return "No address found";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        GpsLocation.latitude = _location.getLatitude();
        GpsLocation.longitude = _location.getLongitude();
        GpsLocation.bearing = _location.getBearing();
        GpsLocation.altitude = _location.getAltitude();
        GpsLocation.speed = _location.getSpeed();
        GpsLocation.address = result;
    }

    private String googleMapsAPIRequest() {
        try {
            JSONObject json = getLocationInfo(_location.getLatitude(), _location.getLongitude());
            return json.getString("formatted_address");
        } catch (Exception e) {
            Utils.sendDebugMessageToServer(_context, "GpsLocationAsyncTask.googleMapsAPIRequest", e.getMessage());
            return "Unavailable";
        }
    }


}
