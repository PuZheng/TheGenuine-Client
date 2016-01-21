package com.puzheng.lejian.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xc on 13-12-4.
 */
public class SPUResponse implements Parcelable {

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
    private SPU spu;
    @SerializedName("same_type_recommendations_cnt")
    private int sameTypeRecommendationsCnt;
    @SerializedName("same_vendor_recommendations_cnt")
    private int sameVendorRecommendationsCnt;
    @SerializedName("comments_cnt")
    private int commentsCnt;
    private boolean favored;
    private int distance;

    public SPUResponse(SPU spu, int sameTypeRecommendationsCnt, int sameVendorRecommendationsCnt, int commentsCnt, boolean favored, int distance) {
        this.spu = spu;
        this.sameTypeRecommendationsCnt = sameTypeRecommendationsCnt;
        this.sameVendorRecommendationsCnt = sameVendorRecommendationsCnt;
        this.commentsCnt = commentsCnt;
        this.favored = favored;
        this.distance = distance;
    }

    public SPUResponse(Parcel source) {
        spu = new SPU(source);
        sameTypeRecommendationsCnt = source.readInt();
        sameVendorRecommendationsCnt = source.readInt();
        commentsCnt = source.readInt();
        favored = Boolean.parseBoolean(source.readString());
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

    public int getSameTypeRecommendationsCnt() {
        return sameTypeRecommendationsCnt;
    }

    public SPU getSPU() {
        return spu;
    }

    public int getSameVendorRecommendationsCnt() {
        return sameVendorRecommendationsCnt;
    }

    public boolean isFavored() {
        return favored;
    }

    public void setFavored(boolean favored) {
        this.favored = favored;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        spu.writeToParcel(dest, flags);
        dest.writeInt(sameTypeRecommendationsCnt);
        dest.writeInt(sameVendorRecommendationsCnt);
        dest.writeInt(commentsCnt);
        dest.writeString(String.valueOf(favored));
        dest.writeInt(distance);
    }

}
