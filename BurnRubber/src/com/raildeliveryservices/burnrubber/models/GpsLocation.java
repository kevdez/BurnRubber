package com.raildeliveryservices.burnrubber.models;

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

            /**
             * Checks for an 'unavailable' address and does some reverse geocoding through the internet.
             * -Kevin H
             * 1/7/15
             */
            if(address.equals("Unavailable"))
            {
                // get lat and lng value
                JSONObject ret = getLocationInfo(latitude, longitude);
                JSONObject location;
                try {
                    //Get JSON Array called "results" and then get the 0th complete object as JSON
                    location = ret.getJSONArray("results").getJSONObject(0);
                    // Get the value of the attribute whose name is "formatted_string"
                    String locationString = location.getString("formatted_address");
                    address = locationString;

                } catch (JSONException e1) {
                }
            }

			locationResult += "Address:" + address + ";";
		}
		
		return locationResult;
	}

    public static JSONObject getLocationInfo( double lat, double lng) {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
