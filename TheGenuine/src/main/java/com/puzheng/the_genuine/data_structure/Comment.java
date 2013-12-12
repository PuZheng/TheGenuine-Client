package com.puzheng.the_genuine.data_structure;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by xc on 13-11-27.
 */
public class Comment {

    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("user_avatar")
    private String userSmallAvatar;
    private String content;
    @SerializedName("create_time")
    private Date date;
    private float rating;

    public Comment(int id, int userId, String userName, String userSmallAvatar,
                   String content, Date date, float rating) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userSmallAvatar = userSmallAvatar;
        this.content = content;
        this.date = date;
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public float getRating() {
        return rating;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserSmallAvatar() {
        return userSmallAvatar;
    }
}
