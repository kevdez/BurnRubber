package com.raildeliveryservices.burnrubber.tasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.raildeliveryservices.burnrubber.models.GpsLocation;
import com.raildeliveryservices.burnrubber.utils.Utils;

import java.util.List;

public class GpsLocationAsyncTask extends AsyncTask<Location, Void, String> {

	private Context _context;
	private Location _location;
	
	public GpsLocationAsyncTask(Context context) {
		_context = context;
	}
	
	@Override
	protected String doInBackground(Location... params) {
		
		Geocoder geocoder = new Geocoder(_context);
		_location = params[0];
		List<Address> addresses = null;
		
		try {
			addresses = geocoder.getFromLocation(_location.getLatitude(), _location.getLongitude(), 1);
		} catch (Exception e) {
			Utils.sendDebugMessageToServer(_context, "GpsLocationAsyncTask.doInBackground", e.getMessage());
			return "Unavailable";
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
	
}
