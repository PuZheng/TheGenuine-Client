package com.puzheng.lejian.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Retrofit;

/**
 * Created by xc on 13-11-27.
 */
public class Comment implements Parcelable {

    private int id;
    private int spuId;
    private int userId;
    private User user;
    private String content;
    private Date createdAt;
    private float rating;


    public Comment(int id, int spuId, int userId, User user,
                   String content, Date createdAt, float rating) {
        this.id = id;
        this.spuId = spuId;
        this.userId = userId;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
        this.rating = rating;
    }

    protected Comment(Parcel in) {
        id = in.readInt();
        spuId = in.readInt();
        userId = in.readInt();
        user = in.readParcelable(User.class.getClassLoader());
        content = in.readString();
        createdAt = new Date(in.readLong());
        rating = in.readFloat();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(spuId);
        dest.writeInt(userId);
        dest.writeParcelable(user, 0);
        dest.writeString(content);
        dest.writeLong(createdAt.getTime());
        dest.writeFloat(rating);
    }

    public void setUser(User user) {
        this.user = user;
    }


    public static class Builder {


        private int id;
        private User user;
        private String content;
        private Date createAt;
        private float rating;
        private int spuId;
        private int userId;

        public Builder() {

        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder createAt(Date createAt) {
            this.createAt = createAt;
            return this;
        }

        public Builder rating(float rating) {
            this.rating = rating;
            return this;
        }

        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public Comment build() {
            return new Comment(id, spuId, userId, user, content, createAt, rating);
        }

        public Builder spuId(int spuId) {
            this.spuId = spuId;
            return this;
        }
    }

}
