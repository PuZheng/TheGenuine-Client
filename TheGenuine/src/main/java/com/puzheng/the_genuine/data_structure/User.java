package com.puzheng.the_genuine.data_structure;

/**
 * Created by xc on 13-11-29.
 */
public class User {
    private final int id;
    private final String email;
    private final String jwtToken;

    public User(int id, String email, String jwtToken) {
        this.id = id;
        this.email = email;
        this.jwtToken = jwtToken;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getJwtToken() {
        return jwtToken;
    }
}
