package com.puzheng.lejian.model;

import android.net.Uri;

import com.puzheng.lejian.util.ConfigUtil;

public class Retailer {

    private int id;
    private String name;
    private float rating;
    private String desc;
    private String tel;
    private Pic pic;
    private POI poi;

    public Retailer(int id, String name, float rating, String desc, String tel,
                    Pic pic, POI poi) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.desc = desc;
        this.tel = tel;
        this.pic = pic;
        this.poi = poi;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public String getDesc() {
        return desc;
    }

    public String getTel() {
        return tel;
    }

    public Pic getPic() {
        return pic;
    }

    public POI getPoi() {
        return poi;
    }

    public Pic getIcon() {
        return new Pic("",
                Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon()
                        .path("assets/sample1.png").build().toString());
    }

    public static class Builder {
        private int id;
        private String name;
        private float rating;
        private String desc;
        private String tel;
        private Pic pic;
        private POI poi;

        public Builder() {

        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder rating(float rating) {
            this.rating = rating;
            return this;
        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder tel(String tel) {
            this.tel = tel;
            return this;
        }

        public Builder pic(Pic pic) {
            this.pic = pic;
            return this;
        }

        public Builder poi(POI poi) {
            this.poi = poi;
            return this;
        }

        public Retailer build() {
            return new Retailer(id, name, rating, desc, tel, pic, poi);
        }
    }
}
