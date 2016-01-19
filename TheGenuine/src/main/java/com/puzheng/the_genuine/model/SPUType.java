package com.puzheng.the_genuine.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xc on 13-11-28.
 */
public class SPUType {

    private final Pic pic;
    private int id;
    private String name;
    private int weight;
    private boolean enabled;
    private int spuCnt;

    public Pic getPic() {
        return pic;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getSpuCnt() {
        return spuCnt;
    }

    public class Pic {
        String url;
        String path;

        public Pic(String url, String path) {
            this.url = url;
            this.path = path;
        }

        public String getURL() {
            return url;
        }

        public String getPath() {
            return path;
        }
    }


    public SPUType(int id, String name, int weight, boolean enabled,
                   int spuCnt, Pic pic) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.enabled = enabled;
        this.spuCnt = spuCnt;
        this.pic = pic;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
