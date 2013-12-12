package com.puzheng.the_genuine.data_structure;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-12.
 */
public class Favor {
    @SerializedName("create_time")
    private Date date;
    private int id;
    private SPU spu;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    private int distance;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SPU getSpu() {
        return spu;
    }

    public void setSpu(SPU spu) {
        this.spu = spu;
    }
}
