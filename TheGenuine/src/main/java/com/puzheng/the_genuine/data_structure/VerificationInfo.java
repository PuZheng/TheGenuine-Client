package com.puzheng.the_genuine.data_structure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xc on 13-11-19.
 */
public class VerificationInfo implements Parcelable {

    private String name;
    private String id;
    private Date manufactureDate;
    private Date expiredDate;
    private String retailer;
    private String vendor;
    private List<String> picUrlList;

    public VerificationInfo(String name, String id, Date manufactureDate, Date expiredDate,
                            String retailer, String vendor, List<String> picUrlList) {
        this.name = name;
        this.id = id;
        this.manufactureDate = manufactureDate;
        this.expiredDate = expiredDate;
        this.retailer = retailer;
        this.vendor = vendor;
        this.picUrlList = new ArrayList<String>();
        this.picUrlList.addAll(picUrlList);
    }

    public VerificationInfo(Parcel source) {
        name = source.readString();
        id = source.readString();
        manufactureDate = new Date(source.readLong());
        expiredDate = new Date(source.readLong());
        retailer = source.readString();
        vendor = source.readString();
        picUrlList = new ArrayList<String>();
        source.readStringList(picUrlList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeLong(manufactureDate.getTime());
        dest.writeLong(expiredDate.getTime());
        dest.writeString(retailer);
        dest.writeString(vendor);
        dest.writeStringList(picUrlList);
    }

    public static final Creator<VerificationInfo> CREATOR = new Creator<VerificationInfo>() {
        @Override
        public VerificationInfo createFromParcel(Parcel source) {
            return new VerificationInfo(source);
        }

        @Override
        public VerificationInfo[] newArray(int size) {
            return new VerificationInfo[0];
        }
    };
}
