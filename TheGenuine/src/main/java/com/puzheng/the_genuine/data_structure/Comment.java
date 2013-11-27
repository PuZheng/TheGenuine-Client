package com.puzheng.the_genuine.data_structure;

import java.util.Date;

/**
 * Created by xc on 13-11-27.
 */
public class Comment {

    private int id;
    private int userId;
    private String userName;
    private String userSmallAvatar;
    private String content;
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


    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserSmallAvatar() {
        return userSmallAvatar;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public float getRating() {
        return rating;
    }
}
