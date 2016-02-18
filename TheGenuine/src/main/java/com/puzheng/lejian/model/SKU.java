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
    private String token;
    private String checksum;
    private int verifyCount;
    private Date lastVerifiedAt;
    private Date productionDate;
    private Date expireDate;
    private SPU spu;

    public SKU(Parcel in) {
        id = in.readInt();
        token = in.readString();
        checksum = in.readString();
        verifyCount = in.readInt();
        lastVerifiedAt = new Date(in.readLong());
        productionDate = new Date(in.readLong());
        expireDate = new Date(in.readLong());
        spu = in.readParcelable(SPU.class.getClassLoader());
    }

    public SKU(int id, String token, String checksum, int verifyCount, Date lastVerifiedAt, Date productionDate, Date expireDate, SPU spu) {
        this.id = id;
        this.token = token;
        this.checksum = checksum;
        this.verifyCount = verifyCount;
        this.lastVerifiedAt = lastVerifiedAt;
        this.productionDate = productionDate;
        this.expireDate = expireDate;
        this.spu = spu;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(token);
        dest.writeString(checksum);
        dest.writeInt(verifyCount);
        dest.writeLong(lastVerifiedAt.getTime());
        dest.writeLong(productionDate.getTime());
        dest.writeLong(expireDate.getTime());
        dest.writeParcelable(spu, 0);
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

    public Date getProductionDate() {
        return productionDate;
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

    public String getToken() {
        return token;
    }

    public int getVerifyCount() {
        return verifyCount;
    }

    public Date getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public static class Builder {
        private int id;
        private String token;
        private String checksum;
        private int verifyCount;
        private Date lastVerifiedAt;
        private Date productionDate;
        private Date expireDate;
        private SPU spu;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder checksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public Builder verifyCount(int verifyCount) {
            this.verifyCount = verifyCount;
            return this;
        }

        public Builder lastVerifiedAt(Date lastVerifiedAt) {
            this.lastVerifiedAt = lastVerifiedAt;
            return this;
        }

        public Builder productionDate(Date productionDate) {
            this.productionDate = productionDate;
            return this;
        }

        public Builder expireDate(Date expireDate) {
            this.expireDate = expireDate;
            return this;
        }

        public Builder spu(SPU spu) {
            this.spu = spu;
            return this;
        }

        public SKU build() {
            return new SKU(id, token, checksum, verifyCount, lastVerifiedAt, productionDate, expireDate, spu);
        }
    }
}
