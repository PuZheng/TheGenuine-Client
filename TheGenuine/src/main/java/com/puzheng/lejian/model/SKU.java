package com.puzheng.lejian.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by xc on 13-12-7.
 */
public class SKU implements Parcelable {

    private int id;
    private SPU spu;
    @SerializedName("manufacture_time")
    private Date manufactureDate;
    @SerializedName("expire_time")
    private Date expireDate;

    private String checksum;
    public SKU(int id, SPU spu, Date manufactureDate, Date expireDate) {
        this.id = id;
        this.spu = spu;
        this.manufactureDate = manufactureDate;
        this.expireDate = expireDate;
    }

    public SKU(Parcel source) {
        id = source.readInt();
        //spu = source.readParcelable(SPU.class.getClassLoader());
        spu = new SPU(source);
        manufactureDate = new Date(source.readLong());
        expireDate = new Date(source.readLong());
        checksum = source.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        spu.writeToParcel(dest, flags);
        dest.writeLong(manufactureDate.getTime());
        dest.writeLong(expireDate.getTime());
        dest.writeString(checksum);
    }

    public static final Creator<SKU> CREATOR = new Creator<SKU>() {
        @Override
        public SKU createFromParcel(Parcel source) {
            return new SKU(source);
        }

        @Override
        public SKU[] newArray(int size) {
            return new SKU[0];
        }
    };

    public SPU getSPU() {
        return spu;
    }

    public Date getManufactureDate() {
        return manufactureDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public int getId() {
        return id;
    }

    public String getChecksum() {
        return checksum;
    }
}
