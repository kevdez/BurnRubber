package com.raildeliveryservices.burnrubber.models;

import com.raildeliveryservices.burnrubber.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.IOException;

public class GpsLocation {
	
	public static double latitude;
	public static double longitude;
	public static double bearing;
	public static double altitude;
	public static double speed;
	public static String address;
		
	public static String getLocationString() {
		
		String locationResult = "";
		
		if (latitude != 0 && longitude != 0) {
			locationResult += "Latitude:" + latitude + ";";
			locationResult += "Longitude:" + longitude + ";";
			locationResult += "Bearing:" + bearing + ";";
			locationResult += "Altitude:" + altitude + ";";
			locationResult += "Speed:" + speed + ";";
            locationResult += "Address:" + address + ";";
		}
		return locationResult;
	}
}
