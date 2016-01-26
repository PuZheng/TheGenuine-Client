package com.puzheng.lejian.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.puzheng.lejian.util.ConfigUtil;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by xc on 13-11-29.
 */
public class User implements Parcelable {
    private final int id;
    private final String email;
    private final String token;

    public User(int id, String email, String token) {
        this.id = id;
        this.email = email;
        this.token = token;
    }

    protected User(Parcel in) {
        id = in.readInt();
        email = in.readString();
        token = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(token);
    }
}
