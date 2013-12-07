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

    public Vendor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private String name;

    public Vendor(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}
