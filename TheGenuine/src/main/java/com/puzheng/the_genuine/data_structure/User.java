package com.puzheng.the_genuine.data_structure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xc on 13-11-29.
 */
public class User {
    @SerializedName("user_id") private final int id;
    private final String name;
    private final String token;

    public User(int id, String name, String token) {
        this.id = id;
        this.name = name;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }
}
