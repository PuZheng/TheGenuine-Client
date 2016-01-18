package com.puzheng.the_genuine.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xc on 13-11-28.
 */
public class SPUType {

    private int id;
    private String name;
    @SerializedName("pic_url")
    private String picUrl;

    public SPUType(int id, String name, String picUrl) {
        this.id = id;
        this.name = name;
        this.picUrl = picUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicUrl() {
        return picUrl;
    }
}
