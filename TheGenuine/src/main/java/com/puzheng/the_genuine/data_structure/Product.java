package com.puzheng.the_genuine.data_structure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xc on 13-12-4.
 */
public class Product implements Parcelable {
    private int id;
    private String name;
    private String code;
    private int vendorId;
    private String vendorName;
    private List<String> picUrlList;
    private float rating;


    public Product(int id, String name, String code, int vendorId, String vendorName, List<String> picUrlList, float rating) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.picUrlList = picUrlList;
        this.rating = rating;
    }

    public Product(Parcel source) {
        id = source.readInt();
        name = source.readString();
        code = source.readString();
        vendorId = source.readInt();
        vendorName = source.readString();
        picUrlList = new ArrayList<String>();
        source.readStringList(picUrlList);
        rating = source.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeInt(vendorId);
        dest.writeString(vendorName);
        dest.writeStringList(picUrlList);
        dest.writeFloat(rating);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getVendorId() {
        return vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public List<String> getPicUrlList() {
        return picUrlList;
    }

    public float getRating() {
        return rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[0];
        }
    };
}
