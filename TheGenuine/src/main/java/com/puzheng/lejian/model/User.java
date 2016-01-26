package com.puzheng.lejian.model;

import android.net.Uri;

import com.puzheng.lejian.util.ConfigUtil;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by xc on 13-11-29.
 */
public class User {
    private final int id;
    private final String email;
    private final String token;

    public User(int id, String email, String token) {
        this.id = id;
        this.email = email;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public Uri getAvatar() {
        return Uri.parse(ConfigUtil.getInstance().getBackend()).
                buildUpon().path("assets/default-avatar.png").build();
    }


}
