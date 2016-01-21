package com.puzheng.lejian.model;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-27.
 */
public class Store {
    private int id;
    private String icon;
    private String logo;
    private String name;
    private float rating;
    private String address;
    private float longitude;
    private float latitude;
    private String desc;

    public Store(int id, String name, String icon, String logo, float rating, String address, float longitude, float latitude, String desc) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.logo = logo;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getRating() {
        return rating;
    }
}
