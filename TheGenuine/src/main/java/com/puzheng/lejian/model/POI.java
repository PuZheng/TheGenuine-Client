package com.puzheng.lejian.model;

public class POI {
    private float lng;
    private float lat;
    private String addr;
    private int distance;

    public POI(float lng, float lat, String addr, int distance) {
        this.lng = lng;
        this.lat = lat;
        this.addr = addr;
        this.distance = distance;
    }

    public String getAddr() {
        return addr;
    }

    public int getDistance() {
        return distance;
    }
}
