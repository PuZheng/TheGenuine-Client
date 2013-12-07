package com.puzheng.the_genuine.data_structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准化产品单元
 * Created by xc on 13-12-4.
 */
public class SPU implements Parcelable {
    public static final Creator<SPU> CREATOR = new Creator<SPU>() {
        @Override
        public SPU createFromParcel(Parcel source) {
            return new SPU(source);
        }

        @Override
        public SPU[] newArray(int size) {
            return new SPU[0];
        }
    };
    private int id;
    private String name;
    private String code;
    private Vendor vendor;
    @SerializedName("pic_url_list")
    private List<String> picUrlList;
    private float rating;

    public SPU(int id, String name, String code, Vendor vendor, List<String> picUrlList, float rating) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.vendor = vendor;
        this.picUrlList =  new ArrayList<String>();
        if (picUrlList != null) {
            this.picUrlList.addAll(picUrlList);
        }
        this.rating = rating;
    }

    public SPU(Parcel source) {
        id = source.readInt();
        name = source.readString();
        code = source.readString();
        vendor = source.readParcelable(Vendor.class.getClassLoader());
        picUrlList = new ArrayList<String>();
        source.readStringList(picUrlList);
        rating = source.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeParcelable(vendor, flags);
        dest.writeStringList(picUrlList);
        dest.writeFloat(rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getPicUrlList() {
        return picUrlList;
    }

    public float getRating() {
        return rating;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public int getVendorId() {
        return vendor.getId();
    }

    public String getVendorName() {
        return vendor.getName();
    }


}
