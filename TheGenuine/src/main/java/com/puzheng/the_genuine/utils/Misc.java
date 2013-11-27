package com.puzheng.the_genuine.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xc on 13-11-21.
 */
public class Misc {
    public static String humanizeDistance(int distance) {
        if (distance < 1000) {
            return String.valueOf(distance) + "米";
        }
        return String.valueOf(distance/1000) + "千米";
    }

    public static String humanizeFavorCnt(int favorCnt) {
        if (favorCnt < 1000) {
            return String.valueOf(favorCnt);
        }
        if (favorCnt < 10000) {
            return String.valueOf(favorCnt/1000) + "千+";
        }
        return String.valueOf(favorCnt/10000) + "万+";
    }

    public static String getMd5Hash(String input) {
        if (isEmptyString(input)) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
                md5 = "0" + md5;

            return md5;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static boolean isEmptyString(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isExternalStorageRemovable() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD || Environment.isExternalStorageRemovable();
    }

    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            File ret = context.getExternalCacheDir();
            if (ret != null) {
                return ret;
            }
        }
        File ret = new File(getStorageDir() + "cache/");
        ret.mkdirs();
        return ret;
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static String getStorageDir() {
        return Environment.getExternalStorageDirectory() + "/Android/data/TheGenium/";
    }
}
