package com.puzheng.the_genuine.data_structure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by xc on 13-11-19.
 */
public class VerificationInfo implements Parcelable {

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
    private SPU SPU;
    private Date manufactureDate;
    private Date expiredDate;
    private int nearbyRecommendationsCnt;
    private int sameVendorRecommendationsCnt;
    private int commentsCnt;

    public VerificationInfo(int productId, String name, String code, Date manufactureDate, Date expiredDate,
                            int vendorId, String vendor, List<String> picUrlList,
                            float rating, int nearbyRecommendationsCnt, int sameVendorRecommendationsCnt, int commentsCnt) {
        this.SPU = new SPU(productId, name, code, new Vendor(vendorId, vendor), picUrlList, rating);
        this.manufactureDate = manufactureDate;
        this.expiredDate = expiredDate;
        this.nearbyRecommendationsCnt = nearbyRecommendationsCnt;
        this.sameVendorRecommendationsCnt = sameVendorRecommendationsCnt;
        this.commentsCnt = commentsCnt;
    }

    public VerificationInfo(Parcel source) {
        SPU = source.readParcelable(SPU.class.getClassLoader());
        manufactureDate = new Date(source.readLong());
        expiredDate = new Date(source.readLong());
        nearbyRecommendationsCnt = source.readInt();
        sameVendorRecommendationsCnt = source.readInt();
        commentsCnt = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getCode() {
        return SPU.getCode();
    }

    public int getCommentsCnt() {
        return commentsCnt;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public Date getManufactureDate() {
        return manufactureDate;
    }

    public String getName() {
        return SPU.getName();
    }

    public int getNearbyRecommendationsCnt() {
        return nearbyRecommendationsCnt;
    }

    public List<String> getPicUrlList() {
        return SPU.getPicUrlList();
    }

    public int getProductId() {
        return SPU.getId();
    }

    public float getRating() {
        return SPU.getRating();
    }

    public int getSameVendorRecommendationsCnt() {
        return sameVendorRecommendationsCnt;
    }

    public int getVendorId() {
        return SPU.getVendor().getId();
    }

    public String getVendorName() {
        return SPU.getVendor().getName();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(SPU, flags);
        dest.writeLong(manufactureDate.getTime());
        dest.writeLong(expiredDate.getTime());
        dest.writeInt(nearbyRecommendationsCnt);
        dest.writeInt(sameVendorRecommendationsCnt);
        dest.writeInt(commentsCnt);
    }
}
