package com.puzheng.lejian.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xc on 13-11-21.
 */
public class Misc {
    private static final String TAG = "Misc";

    public static void assertDirExists(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG, "can't create directory: " + dir);
            }
        }
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

    public static String getMd5Hash(String input) {
        if (TextUtils.isEmpty(input)) {
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

    public static Pair<String, Integer> getServerAddress(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ip = sharedPreferences.getString("server_ip", "115.29.232.202");
        int port = sharedPreferences.getInt("server_port", 8000);
        return new Pair<String, Integer>(ip, port);
    }

    public static String getStorageDir() {
        return Environment.getExternalStorageDirectory() + "/Android/data/TheGenium/";
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isExternalStorageRemovable() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD || Environment.isExternalStorageRemovable();
    }


    public static String truncate(String s, int maxSize) {
        if (s.length() <= maxSize) {
            return s;
        } else {
            return s.substring(0, maxSize - 2) + "..";
        }
    }
    public static boolean isNetworkException(Exception e) {
        return e instanceof IOException;
    }

    public static Location getLastLocation(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("location", Context.MODE_PRIVATE);
        String location = preferences.getString("location", null);
        Gson gson = new Gson();
        return gson.fromJson(location, Location.class);
    }

    public static void storeLastLocation(Location location, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("location", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        editor.putString("location", gson.toJson(location));
        editor.commit();
    }
}
