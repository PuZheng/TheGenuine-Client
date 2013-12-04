package com.puzheng.the_genuine.data_structure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xc on 13-12-4.
 */
public class ProductResponse implements Parcelable {

    private Product product;
    private int nearbyRecommendationsCnt;
    private int sameVendorRecommendationsCnt;
    private int commentsCnt;

    public ProductResponse(Product product, int nearbyRecommendationsCnt, int sameVendorRecommendationsCnt, int commentsCnt) {
        this.product = product;
        this.nearbyRecommendationsCnt = nearbyRecommendationsCnt;
        this.sameVendorRecommendationsCnt = sameVendorRecommendationsCnt;
        this.commentsCnt = commentsCnt;
    }

    public ProductResponse(Parcel source) {
        product = new Product(source);
        nearbyRecommendationsCnt = source.readInt();
        sameVendorRecommendationsCnt = source.readInt();
        commentsCnt = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        product.writeToParcel(dest, flags);
        dest.writeInt(nearbyRecommendationsCnt);
        dest.writeInt(sameVendorRecommendationsCnt);
        dest.writeInt(commentsCnt);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public Product getProduct() {
        return product;
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

    public static final Creator<ProductResponse> CREATOR = new Creator<ProductResponse>() {
        @Override
        public ProductResponse createFromParcel(Parcel source) {
            return new ProductResponse(source);
        }

        @Override
        public ProductResponse[] newArray(int size) {
            return new ProductResponse[0];
        }
    };
}
