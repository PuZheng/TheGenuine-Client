package com.puzheng.the_genuine.data_structure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-06.
 */
public class Vendor implements Parcelable {
    public static final Creator<Vendor> CREATOR = new Creator<Vendor>() {
        @Override
        public Vendor createFromParcel(Parcel source) {
            return new Vendor(source);
        }

        @Override
        public Vendor[] newArray(int size) {
            return new Vendor[0];
        }
    };
    private int id;
    private String tel;
    private String name;
    private String address;
    private String website;
    private String weibo;
    private String weixin;

    public Vendor(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        tel = parcel.readString();
        address = parcel.readString();
        website = parcel.readString();
        weibo = parcel.readString();
        weixin = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAddress() {
        return address;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTel() {
        return tel;
    }

    public String getWebsite() {
        return website;
    }

    public String getWeibo() {
        return weibo;
    }

    public String getWeixin() {
        return weixin;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(tel);
        dest.writeString(address);
        dest.writeString(website);
    }
}
