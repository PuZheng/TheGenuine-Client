package com.puzheng.humanize;

import android.content.Context;

public class Humanize {

    private final Context context;

    public Humanize(Context context) {
        this.context = context;
    }

    public static Humanize with(Context context) {
        return new Humanize(context);
    }

    public String distance(int distance) {
        if (distance < 1000) {
            return String.valueOf(distance) + context.getString(R.string.meter);
        }
        return String.valueOf(distance / 1000) + context.getString(R.string.kilometers);
    }

    public String num(int favorCnt) {
        if (favorCnt < 1000) {
            return String.valueOf(favorCnt);
        }
        return String.valueOf(favorCnt / 1000) + context.getString(R.string.thousand);
    }
}
