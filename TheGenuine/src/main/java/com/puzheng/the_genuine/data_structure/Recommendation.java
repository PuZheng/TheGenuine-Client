package com.puzheng.the_genuine.data_structure;

/**
 * Created by xc on 13-11-21.
 */
public class Recommendation {

    private int productId;
    private String productName;
    private int distance;
    private int favorCnt;
    private String picUrl;
    private float rate;

    public Recommendation(int productId, String productName, int distance, int favorCnt, String picUrl, float rate) {
        this.productId = productId;
        this.productName = productName;
        this.distance = distance;
        this.favorCnt = favorCnt;
        this.picUrl = picUrl;
        this.rate = rate;
    }

    public int getProductId() {
        return productId;
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
}
