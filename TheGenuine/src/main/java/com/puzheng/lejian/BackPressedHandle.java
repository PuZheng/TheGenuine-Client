package com.puzheng.lejian;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-29.
 */
public class BackPressedHandle {
    private boolean doubleBackToExitPressedOnce = false;

    public void doBackPressed(BackPressedInterface i, Context context) {
        if (doubleBackToExitPressedOnce) {
            i.doBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(context, R.string.exit_press_back_twice_message, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
