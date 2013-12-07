package com.puzheng.the_genuine.data_structure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by xc on 13-12-7.
 */
public class SKU implements Parcelable {

    private SPU spu;
    private Date manufactureDate;
    private Date expireDate;
    private Date expiredDate;

    public SKU(SPU spu, Date manufactureDate, Date expireDate) {
        this.spu = spu;
        this.manufactureDate = manufactureDate;
        this.expireDate = expireDate;
    }

    public SKU(Parcel source) {
        spu = new SPU(source);
        manufactureDate = new Date(source.readLong());
        expireDate = new Date(source.readLong());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        spu.writeToParcel(dest, flags);
        dest.writeLong(manufactureDate.getTime());
        dest.writeLong(expireDate.getTime());
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

    public SPU getSpu() {
        return spu;
    }

    public Date getManufactureDate() {
        return manufactureDate;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }
}
