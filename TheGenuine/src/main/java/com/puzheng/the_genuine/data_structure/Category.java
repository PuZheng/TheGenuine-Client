package com.puzheng.the_genuine.data_structure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xc on 13-11-28.
 */
public class Category {

    private int id;
    private String name;
    @SerializedName("pic_url")
    private String picUrl;

    public Category(int id, String name, String picUrl) {
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
