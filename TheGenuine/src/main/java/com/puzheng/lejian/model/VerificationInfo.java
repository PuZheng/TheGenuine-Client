package com.puzheng.lejian.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

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
    private SKU sku;
    @SerializedName("same_type_recommendations_cnt")
    private int sameTypeRecommendationsCnt;
    @SerializedName("same_vendor_recommendations_cnt")
    private int sameVendorRecommendationsCnt;
    @SerializedName("comments_cnt")
    private int commentsCnt;
    private boolean favored;
    @SerializedName("verify_cnt")
    private int verifyCnt;

    @SerializedName("last_verify_time")
    private Date lastVerifyTime;

    private int distance;
    public VerificationInfo(SKU sku, int sameTypeRecommendationsCnt, int sameVendorRecommendationsCnt, int commentsCnt, boolean favored, int distance) {
        this.sku = sku;
        this.sameTypeRecommendationsCnt = sameTypeRecommendationsCnt;
        this.sameVendorRecommendationsCnt = sameVendorRecommendationsCnt;
        this.commentsCnt = commentsCnt;
        this.favored = favored;
        this.distance = distance;
    }

    public VerificationInfo(Parcel source) {
        sku = source.readParcelable(SKU.class.getClassLoader());
        sameTypeRecommendationsCnt = source.readInt();
        sameVendorRecommendationsCnt = source.readInt();
        commentsCnt = source.readInt();
        favored = Boolean.parseBoolean(source.readString());
        verifyCnt = source.readInt();
        lastVerifyTime = (Date) source.readSerializable();
        distance = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getCommentsCnt() {
        return commentsCnt;
    }

    public int getDistance() {
        return distance;
    }

    public Date getLastVerifyTime() {
        return lastVerifyTime;
    }

    public int getSameTypeRecommendationsCnt() {
        return sameTypeRecommendationsCnt;
    }

    public SKU getSKU() {
        return sku;
    }

    public int getSameVendorRecommendationsCnt() {
        return sameVendorRecommendationsCnt;
    }

    public int getVerifyCnt() {
        return verifyCnt;
    }

    public boolean isFavored() {
        return favored;
    }

    public void setFavored(boolean favored) {
        this.favored = favored;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(sku, flags);
        dest.writeInt(sameTypeRecommendationsCnt);
        dest.writeInt(sameVendorRecommendationsCnt);
        dest.writeInt(commentsCnt);
        dest.writeString(String.valueOf(favored));
        dest.writeInt(verifyCnt);
        dest.writeSerializable(lastVerifyTime);
        dest.writeInt(distance);
    }
}
