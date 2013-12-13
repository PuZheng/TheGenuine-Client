package com.puzheng.the_genuine.data_structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xc on 13-12-4.
 */
public class SPUResponse implements Parcelable {

    private SPU spu;
    @SerializedName("nearby_recommendations_cnt")
    private int nearbyRecommendationsCnt;
    @SerializedName("same_vendor_recommendations_cnt")
    private int sameVendorRecommendationsCnt;
    @SerializedName("comments_cnt")
    private int commentsCnt;

    public SPUResponse(SPU spu, int nearbyRecommendationsCnt, int sameVendorRecommendationsCnt, int commentsCnt) {
        this.spu = spu;
        this.nearbyRecommendationsCnt = nearbyRecommendationsCnt;
        this.sameVendorRecommendationsCnt = sameVendorRecommendationsCnt;
        this.commentsCnt = commentsCnt;
    }

    public SPUResponse(Parcel source) {
        spu = new SPU(source);
        nearbyRecommendationsCnt = source.readInt();
        sameVendorRecommendationsCnt = source.readInt();
        commentsCnt = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        spu.writeToParcel(dest, flags);
        dest.writeInt(nearbyRecommendationsCnt);
        dest.writeInt(sameVendorRecommendationsCnt);
        dest.writeInt(commentsCnt);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public SPU getSPU() {
        return spu;
    }

    public int getNearbyRecommendationsCnt() {
        return nearbyRecommendationsCnt;
    }

    public int getSameVendorRecommendationsCnt() {
        return sameVendorRecommendationsCnt;
    }

    public int getCommentsCnt() {
        return commentsCnt;
    }

    public static final Creator<SPUResponse> CREATOR = new Creator<SPUResponse>() {
        @Override
        public SPUResponse createFromParcel(Parcel source) {
            return new SPUResponse(source);
        }

        @Override
        public SPUResponse[] newArray(int size) {
            return new SPUResponse[0];
        }
    };

}
