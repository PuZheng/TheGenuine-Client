                                            package com.puzheng.lejian.model;

import android.os.Parcel;
import android.os.Parcelable;

                                            /**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-06.
 */
public class Vendor implements Parcelable {
    public static final Creator<Vendor> CREATOR = new Creator<Vendor>() {
        @Override
        public Vendor createFromParcel(Parcel source) {
            return new Vendor(source);
        }

        @Override
        public Vendor[] newArray(int size) {
            return new Vendor[0];
        }
    };
    private int id;
    private String name;
    private String desc;
    private String tel;
    private String addr;
    private String email;
    private String website;
    private String weiboUserId;
    private String weiboHomepage;
    private String weixinAccount;
    private boolean enabled;

    public Vendor(int id, String name, String desc, String tel, String addr,
                  String email, String website, String weiboUserId,
                  String weiboHomepage, String weixinAccount, boolean enabled) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.tel = tel;
        this.addr = addr;
        this.email = email;
        this.website = website;
        this.weiboUserId = weiboUserId;
        this.weiboHomepage = weiboHomepage;
        this.weixinAccount = weixinAccount;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getTel() {
        return tel;
    }

    public String getAddr() {
        return addr;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getWeiboUserId() {
        return weiboUserId;
    }

    public String getWeiboHomepage() {
        return weiboHomepage;
    }

    public String getWeixinAccount() {
        return weixinAccount;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Vendor(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        desc = parcel.readString();
        tel = parcel.readString();
        addr = parcel.readString();
        email = parcel.readString();
        website = parcel.readString();
        weiboUserId = parcel.readString();
        weiboHomepage = parcel.readString();
        weixinAccount = parcel.readString();
        enabled = parcel.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(tel);
        dest.writeString(addr);
        dest.writeString(email);
        dest.writeString(website);
        dest.writeString(weiboUserId);
        dest.writeString(weiboHomepage);
        dest.writeString(weixinAccount);
        dest.writeInt(enabled? 1: 0);
    }

    public static class Builder {
        private int id;
        private String name;
        private String desc;
        private String tel;
        private String addr;
        private String email;
        private String website;
        private String weiboUserId;
        private String weiboHomepage;
        private String weixinAccount;
        private boolean enabled;

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

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder tel(String tel) {
            this.tel = tel;
            return this;
        }

        public Builder addr(String addr) {
            this.addr = addr;
            return this;
        }

        public Builder website(String website) {
            this.website = website;
            return this;
        }

        public Builder weiboUserId(String weiboUserId) {
            this.weiboUserId = weiboUserId;
            return this;
        }

        public Builder weiboHomepage(String weiboHomepage) {
            this.weiboHomepage = weiboHomepage;
            return this;
        }
        public Builder weixinAccount(String weixinAccount) {
            this.weixinAccount = weixinAccount;
            return this;
        }
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Vendor build() {
            return new Vendor(id, name, desc, tel, addr, email, website, weiboUserId,
                    weiboHomepage, weixinAccount, enabled);
        }
    }
}
