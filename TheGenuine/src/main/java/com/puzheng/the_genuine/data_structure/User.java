package com.puzheng.the_genuine.data_structure;

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
}
