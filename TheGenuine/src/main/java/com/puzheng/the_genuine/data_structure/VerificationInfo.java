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

    private int id;
    private String name;
    private String code;
    private Date manufactureDate;
    private Date expiredDate;
    private String retailer;
    private int vendorId;
    private String vendorName;
    private List<String> picUrlList;
    private float rating;
    private int nearbyRecommendationsCnt;
    private int sameVendorRecommendationsCnt;
    private int commentsCnt;

    public VerificationInfo(int id, String name, String code, Date manufactureDate, Date expiredDate,
                            String retailer, int vendorId, String vendor, List<String> picUrlList,
                            float rating, int nearbyRecommendationsCnt, int sameVendorRecommendationsCnt, int commentsCnt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.manufactureDate = manufactureDate;
        this.expiredDate = expiredDate;
        this.retailer = retailer;
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
        id = source.readInt();
        name = source.readString();
        code = source.readString();
        manufactureDate = new Date(source.readLong());
        expiredDate = new Date(source.readLong());
        retailer = source.readString();
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
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeLong(manufactureDate.getTime());
        dest.writeLong(expiredDate.getTime());
        dest.writeString(retailer);
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

    public int getId() {
        return id;
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
}
