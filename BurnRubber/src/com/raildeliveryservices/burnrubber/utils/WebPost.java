package com.raildeliveryservices.burnrubber.utils;

import android.text.TextUtils;
import android.util.Base64;

import com.raildeliveryservices.burnrubber.WebServiceConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebPost {

    private String _url;
    private String _json;

    public WebPost(String url) {
        _url = url;
    }

    public void setJson(String json) {
        _json = json;
    }

    public JSONObject Post() throws IOException, JSONException {

        StringEntity stringEntity;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(_url);

        httpPost.setHeader("Authorization", getBase64Authorization());

        if (!TextUtils.isEmpty(_json)) {
            stringEntity = new StringEntity(_json);
            httpPost.setEntity(stringEntity);
        }

        HttpResponse httpResponse = httpClient.execute(httpPost);
        InputStream inputStream = httpResponse.getEntity().getContent();

        return new JSONObject(convertInputStreamToString(inputStream));
    }

    private String getBase64Authorization() {
        String login = WebServiceConstants.WEB_SERVICE_LOGIN + ":" + WebServiceConstants.WEB_SERVICE_PASSWORD;
        return "Basic " + Base64.encodeToString(login.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        inputStream.close();
        return result;
    }
}
