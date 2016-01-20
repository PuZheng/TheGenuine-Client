package com.puzheng.the_genuine.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
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

    public SPU(int id, String name, String code, Vendor vendor,
               SPUType spuType, float rating, float msrp, String desc,
               List<Pic> pics, Pic icon) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.vendor = vendor;
        this.icon = icon;
        this.msrp = msrp;
        this.rating = rating;
        this.spuType = spuType;
        this.pics = pics;
        this.desc = desc;
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
        dest.writeList(pics);
        dest.writeParcelable(icon, 0);
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

    public float getMsrp() {
        return msrp;
    }

    public void setMsrp(float msrp) {
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

    public static class Pic implements Parcelable {
        private String path;
        private String url;

        private Pic(String path, String url) {
            this.path = path;
            this.url = url;
        }

        protected Pic(Parcel in) {
            path = in.readString();
            url = in.readString();
        }

        public static final Creator<Pic> CREATOR = new Creator<Pic>() {
            @Override
            public Pic createFromParcel(Parcel in) {
                return new Pic(in);
            }

            @Override
            public Pic[] newArray(int size) {
                return new Pic[size];
            }
        };

        public String getPath() {
            return path;
        }

        public String getURL() {
            return url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(path);
            dest.writeString(path);
        }
    }
}
