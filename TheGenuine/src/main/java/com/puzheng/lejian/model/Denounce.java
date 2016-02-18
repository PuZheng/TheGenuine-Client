package com.puzheng.lejian.model;

/**
 * Created by xc on 16-2-18.
 */
public class Denounce {

    private String reason;
    private String token;
    private double lng;
    private double lat;

    public Denounce(String reason, String token, double lng, double lat) {
        this.reason = reason;
        this.token = token;
        this.lng = lng;
        this.lat = lat;
    }
}
