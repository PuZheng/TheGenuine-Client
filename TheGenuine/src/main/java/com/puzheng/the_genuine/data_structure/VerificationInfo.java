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

    private int productId;
    private String name;
    private String code;
    private Date manufactureDate;
    private Date expiredDate;
    private int vendorId;
    private String vendorName;
    private List<String> picUrlList;
    private float rating;
    private int nearbyRecommendationsCnt;
    private int sameVendorRecommendationsCnt;
    private int commentsCnt;

    public VerificationInfo(int productId, String name, String code, Date manufactureDate, Date expiredDate,
                            int vendorId, String vendor, List<String> picUrlList,
                            float rating, int nearbyRecommendationsCnt, int sameVendorRecommendationsCnt, int commentsCnt) {
        this.productId = productId;
        this.name = name;
        this.code = code;
        this.manufactureDate = manufactureDate;
        this.expiredDate = expiredDate;
        this.vendorId = vendorId;
        this.vendorName = vendor;
        this.picUrlList = new ArrayList<String>();
        this.picUrlList.addAll(picUrlList);
        this.rating = rating;
        this.nearbyRecommendationsCnt = nearbyRecommendationsCnt;
        this.sameVendorRecommendationsCnt = sameVendorRecommendationsCnt;
        this.commentsCnt = commentsCnt;
    }

    public VerificationInfo(Parcel source) {
        code = source.readString();
        name = source.readString();
        code = source.readString();
        manufactureDate = new Date(source.readLong());
        expiredDate = new Date(source.readLong());
        vendorId = source.readInt();
        vendorName = source.readString();
        picUrlList = new ArrayList<String>();
        source.readStringList(picUrlList);
        rating = source.readFloat();
        nearbyRecommendationsCnt = source.readInt();
        sameVendorRecommendationsCnt = source.readInt();
        commentsCnt = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeLong(manufactureDate.getTime());
        dest.writeLong(expiredDate.getTime());
        dest.writeInt(vendorId);
        dest.writeString(vendorName);
        dest.writeStringList(picUrlList);
        dest.writeFloat(rating);
        dest.writeInt(nearbyRecommendationsCnt);
        dest.writeInt(sameVendorRecommendationsCnt);
        dest.writeInt(commentsCnt);
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

    public int getVendorId() {
        return vendorId;
    }

    public String getCode() {
        return code;
    }

    public float getRating() {
        return rating;
    }

    public int getSameVendorRecommendationsCnt() {
        return sameVendorRecommendationsCnt;
    }

    public int getNearbyRecommendationsCnt() {
        return nearbyRecommendationsCnt;
    }

    public int getCommentsCnt() {
        return commentsCnt;
    }

    public List<String> getPicUrlList() {
        return picUrlList;
    }

    public String getName() {
        return name;
    }

    public String getVendorName() {
        return vendorName;
    }

    public Date getManufactureDate() {
        return manufactureDate;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public int getProductId() {
        return productId;
    }
}
