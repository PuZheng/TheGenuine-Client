package com.puzheng.the_genuine.data_structure;

import android.os.Parcel;
import android.os.Parcelable;

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
    private int nearbyRecommendationsCnt;
    private int sameVendorRecommendationsCnt;
    private int commentsCnt;

    public VerificationInfo(SKU sku, int nearbyRecommendationsCnt, int sameVendorRecommendationsCnt, int commentsCnt) {
        this.sku = sku;
        this.nearbyRecommendationsCnt = nearbyRecommendationsCnt;
        this.sameVendorRecommendationsCnt = sameVendorRecommendationsCnt;
        this.commentsCnt = commentsCnt;
    }

    public VerificationInfo(Parcel source) {
        sku = new SKU(source);
        nearbyRecommendationsCnt = source.readInt();
        sameVendorRecommendationsCnt = source.readInt();
        commentsCnt = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getCommentsCnt() {
        return commentsCnt;
    }

    public int getNearbyRecommendationsCnt() {
        return nearbyRecommendationsCnt;
    }

    public int getSameVendorRecommendationsCnt() {
        return sameVendorRecommendationsCnt;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(sku, flags);
        dest.writeInt(nearbyRecommendationsCnt);
        dest.writeInt(sameVendorRecommendationsCnt);
        dest.writeInt(commentsCnt);
    }

    public SKU getSku() {
        return sku;
    }
}
