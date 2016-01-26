package com.puzheng.lejian.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class SPU implements Parcelable {


    private int id;
    private String name;
    private String code;
    private Vendor vendor;
    private SPUType spuType;
    private float rating;
    private float msrp;
    private String desc;
    private List<Pic> pics;
    private Pic icon;
    private int distance;

    public void setFavored(boolean favored) {
        this.favored = favored;
    }

    private boolean favored;
    private int commentCnt;
    private int favorCnt;

    public SPU(int id, String name, String code, Vendor vendor,
               SPUType spuType, float rating, float msrp, String desc,
               List<Pic> pics, Pic icon, int distance, boolean favored,
               int commentCnt, int favorCnt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.vendor = vendor;
        this.spuType = spuType;
        this.rating = rating;
        this.msrp = msrp;
        this.desc = desc;
        this.pics = pics;
        this.icon = icon;
        this.distance = distance;
        this.favored = favored;
        this.commentCnt = commentCnt;
        this.favorCnt = favorCnt;
    }

    public boolean isFavored() {
        return favored;
    }

    public int getCommentCnt() {
        return commentCnt;
    }

    public int getFavorCnt() {
        return favorCnt;
    }


    public static class Builder {

        private Vendor vendor;
        private int id;
        private String name;
        private String code;
        private SPUType spuType;
        private float rating;
        private float msrp;
        private String desc;
        private List<Pic> pics;
        private Pic icon;
        private int distance;
        private boolean favored;
        private int commentCnt;
        private int favorCnt;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder vendor(Vendor vendor) {
            this.vendor = vendor;
            return this;
        }

        public Builder spuType(SPUType spuType) {
            this.spuType = spuType;
            return this;
        }

        public Builder rating(float rating) {
            this.rating = rating;
            return this;

        }

        public Builder msrp(float msrp) {
            this.msrp = msrp;
            return this;

        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder pics(List<Pic> pics) {
            this.pics = pics;
            return this;
        }

        public Builder icon(Pic icon) {
            this.icon = icon;
            return this;

        }

        public Builder distance(int distance) {
            this.distance = distance;
            return this;
        }

        public Builder favored(boolean favored) {
            this.favored = favored;
            return this;
        }

        public Builder commentCnt(int commentCnt) {
            this.commentCnt = commentCnt;
            return this;
        }

        public Builder favorCnt(int favorCnt) {
            this.favorCnt = favorCnt;
            return this;
        }

        public SPU build() {
            return new SPU(id, name, code, vendor, spuType, rating, msrp, desc,
                    pics, icon, distance, favored, commentCnt, favorCnt);
        }
    }

    protected SPU(Parcel in) {
        id = in.readInt();
        name = in.readString();
        code = in.readString();
        vendor = in.readParcelable(Vendor.class.getClassLoader());
        spuType = in.readParcelable(SPUType.class.getClassLoader());
        rating = in.readFloat();
        msrp = in.readFloat();
        desc = in.readString();
        pics = in.createTypedArrayList(Pic.CREATOR);
        icon = in.readParcelable(Pic.class.getClassLoader());
        distance = in.readInt();
        favored = in.readInt() == 1;
        commentCnt = in.readInt();
        favorCnt = in.readInt();
    }

    public static final Creator<SPU> CREATOR = new Creator<SPU>() {
        @Override
        public SPU createFromParcel(Parcel in) {
            return new SPU(in);
        }

        @Override
        public SPU[] newArray(int size) {
            return new SPU[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeParcelable(vendor, 0);
        dest.writeParcelable(spuType, 0);
        dest.writeFloat(rating);
        dest.writeFloat(msrp);
        dest.writeString(desc);
        dest.writeTypedList(pics);
        dest.writeParcelable(icon, 0);
        dest.writeInt(distance);
        dest.writeInt(favored ? 1 : 0);
        dest.writeInt(commentCnt);
        dest.writeInt(favorCnt);
    }

    public String getCode() {
        return code;
    }

    public Pic getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }

    public float getMSRP() {
        return msrp;
    }

    public void setMSRP(float msrp) {
        this.msrp = msrp;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public int getVendorId() {
        return vendor.getId();
    }

    public String getVendorName() {
        return vendor.getName();
    }

    public SPUType getSpuType() {
        return spuType;
    }

    public String getDesc() {
        return desc;
    }

    public List<Pic> getPics() {
        return pics;
    }

    public int getDistance() {
        return distance;
    }

}
