package com.puzheng.the_genuine.utils;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-12.
 */
public class BadResponseException extends Exception {
    private int statusCode;

    public BadResponseException(int statusCode, String cause) {
        super(cause);
        this.statusCode = statusCode;
    }
}
