package com.puzheng.the_genuine.data_structure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-27.
 */
public class Store {
    private int id;
    @SerializedName("logo")
    private String picUrl;
    private String name;
    private float rating;
    private String address;
    private float longitude;
    private float latitude;
    private String desc;

    public Store(int id, String name, String picUrl, float rating, String address, float longitude, float latitude, String desc) {
        this.id = id;
        this.name = name;
        this.picUrl = picUrl;
        this.rating = rating;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.desc = desc;
    }

    public String getAddress() {
        return address;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getID() {
        return id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
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
