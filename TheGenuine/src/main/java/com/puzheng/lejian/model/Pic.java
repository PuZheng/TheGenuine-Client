package com.puzheng.lejian.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xc on 16-1-24.
 */
public class Pic implements Parcelable {
    private String path;
    private String url;

    public Pic(String path, String url) {
        this.path = path;
        this.url = url;
    }

    protected Pic(Parcel in) {
        path = in.readString();
        url = in.readString();
    }

    public static final Creator<Pic> CREATOR = new Creator<Pic>() {
        @Override
        public Pic createFromParcel(Parcel in) {
            return new Pic(in);
        }

        @Override
        public Pic[] newArray(int size) {
            return new Pic[size];
        }
    };

    public String getPath() {
        return path;
    }

    public String getURL() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(url);
    }
}
