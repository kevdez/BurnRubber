package com.raildeliveryservices.burnrubber.data;

/**
 * Created by nghia on 05/01/2015.
 */
public class WebResponse {
    private ResponseCode responseCode;
    private String message;
    private String requestedURL;

    public WebResponse(ResponseCode responseCode, String message) {
        this.responseCode = responseCode;
        this.message = message;
    }

    public WebResponse() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public String getRequestedURL() {
        return requestedURL;
    }

    public void setRequestedURL(String requestedURL) {
        this.requestedURL = requestedURL;
    }

    public static enum ResponseCode {
        SUCCESS, FAIL
    }
}

