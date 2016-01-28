package com.puzheng.lejian.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-12.
 */
public class Favor {

    private int id;
    private SPU spu;
    private int spuId;
    private int userId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SPU getSPU() {
        return spu;
    }

    public void setSpu(SPU spu) {
        this.spu = spu;
    }
}
