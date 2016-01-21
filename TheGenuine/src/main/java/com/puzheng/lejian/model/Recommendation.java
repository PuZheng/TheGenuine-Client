package com.puzheng.lejian.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xc on 13-11-21.
 */
public class Recommendation {

    @SerializedName("spu_id") int spuId;
    @SerializedName("spu_name") String productName;
    private int distance;
    @SerializedName("favor_cnt") private int favorCnt;
    @SerializedName("pic_url") private String picUrl;
    @SerializedName("msrp") private int priceInCents;
    private float rating;

    public Recommendation(int spuId, String productName, int distance, int favorCnt,
                          String picUrl, float rating, int priceInCents) {
        this.spuId = spuId;
        this.productName = productName;
        this.distance = distance;
        this.favorCnt = favorCnt;
        this.picUrl = picUrl;
        this.rating = rating;
        this.priceInCents = priceInCents;
    }

    public int getSPUId() {
        return spuId;
    }

    public String getProductName() {
        return productName;
    }

    public int getDistance() {
        return distance;
    }

    public int getFavorCnt() {
        return favorCnt;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public int getPriceInYuan() {
        return priceInCents;
    }

    public float getRating() {
        return rating;
    }
}
