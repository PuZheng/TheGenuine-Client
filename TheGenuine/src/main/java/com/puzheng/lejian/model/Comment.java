package com.puzheng.lejian.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by xc on 13-11-27.
 */
public class Comment {

    private final User user;
    private int id;
    private String content;
    private Date createdAt;
    private float rating;

    public Comment(int id, User user,
                   String content, Date createdAt, float rating) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public float getRating() {
        return rating;
    }

    public User getUser() {
        return user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
