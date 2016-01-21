package com.puzheng.lejian.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-12.
 */
public class StoreResponse {
    @SerializedName("retailer")
    private Store store;
    private int distance;

    public int getDistance() {
        return distance;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
