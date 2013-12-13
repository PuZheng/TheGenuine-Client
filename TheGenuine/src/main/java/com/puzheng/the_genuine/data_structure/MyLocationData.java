package com.puzheng.the_genuine.data_structure;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.map.LocationData;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-13.
 */
public class MyLocationData implements Parcelable {

    public static final Creator<MyLocationData> CREATOR = new Creator<MyLocationData>() {
        @Override
        public MyLocationData createFromParcel(Parcel source) {
            return new MyLocationData(source);
        }

        @Override
        public MyLocationData[] newArray(int size) {
            return new MyLocationData[0];
        }
    };
    private double latitude;
    private double longitude;
    private float speed;
    private float direction;
    private float accuracy;
    private int satellitesNum;

    public MyLocationData() {

    }


    public MyLocationData(Parcel parcel) {
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        speed = parcel.readFloat();
        direction = parcel.readFloat();
        accuracy = parcel.readFloat();
        satellitesNum = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getSatellitesNum() {
        return satellitesNum;
    }

    public void setSatellitesNum(int satellitesNum) {
        this.satellitesNum = satellitesNum;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public LocationData toBaiduLocationData() {
        LocationData result = new LocationData();
        result.latitude = latitude;
        result.longitude = longitude;
        result.speed = speed;
        result.direction = direction;
        result.accuracy = accuracy;
        result.satellitesNum = satellitesNum;
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(speed);
        dest.writeFloat(direction);
        dest.writeFloat(accuracy);
        dest.writeInt(satellitesNum);
    }
}
