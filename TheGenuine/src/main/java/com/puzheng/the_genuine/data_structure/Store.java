package com.puzheng.the_genuine.data_structure;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-27.
 */
public class Store {
    private int id;
    private String picUrl;
    private String name;
    private int distance;
    private float rating;
    private String location;

    public Store(int id, String name, String picUrl, float rating, String location, int distance) {
        this.id = id;
        this.name = name;
        this.picUrl = picUrl;
        this.rating = rating;
        this.location = location;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getID() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public float getRating() {
        return rating;
    }
}
