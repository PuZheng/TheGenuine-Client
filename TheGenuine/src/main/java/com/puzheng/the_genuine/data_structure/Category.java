package com.puzheng.the_genuine.data_structure;

/**
 * Created by xc on 13-11-28.
 */
public class Category {

    private int id;
    private String name;
    private int productNum;
    private String picUrl;

    public Category(int id, String name, int productNum, String picUrl) {
        this.id = id;
        this.name = name;
        this.productNum = productNum;
        this.picUrl = picUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getProductNum() {
        return productNum;
    }

    public String getPicUrl() {
        return picUrl;
    }
}
