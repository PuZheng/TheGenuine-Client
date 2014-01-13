package com.puzheng.the_genuine;

import java.util.ArrayList;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-05.
 */
public interface Constants {
    public static final String BAIDU_MAP_KEY = "UTW8RC2pRPD9oGrGn8jXgcnO";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final int INVALID_ARGUMENT = Integer.MIN_VALUE;
    public static final String TAG_LOCATION_DATA = "LOCATION_DATA";
    public static final String TAG_SPU_ID = "SPU_ID";
    public static final String TAG_SPU_NAME = "SPU_NAME";
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final ArrayList<Integer> MARKS = new ArrayList<Integer>() {
        {
            add(R.drawable.icon_mark1);
            add(R.drawable.icon_mark2);
            add(R.drawable.icon_mark3);
            add(R.drawable.icon_mark4);
            add(R.drawable.icon_mark5);
            add(R.drawable.icon_mark6);
            add(R.drawable.icon_mark7);
            add(R.drawable.icon_mark8);
            add(R.drawable.icon_mark9);
            add(R.drawable.icon_mark10);
        }
    };
}