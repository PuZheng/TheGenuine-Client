package com.puzheng.the_genuine.util;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-12.
 */
public class BadResponseException extends Exception {
    private int statusCode;
    private String url;

    public BadResponseException(int statusCode, String url, String cause) {
        super(cause);
        this.url = url;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
