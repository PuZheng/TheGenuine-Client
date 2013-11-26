package com.puzheng.the_genuine.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.puzheng.the_genuine.R;

/**
 * Created by xc on 13-11-26.
 */
public class NavBar extends LinearLayout {
    private static final int GO_HOME = 0;
    private static final int GENUINES = 1;
    private static final int AROUND = 2;
    private static final int FAVOR = 3;
    private static final int ACCOUNT = 4;

    public NavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.nav_bar, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.NavBar, 0, 0);
        int tab = a.getInteger(R.styleable.NavBar_enabledTab, GO_HOME);
        ImageButton imageButtonActivitated = null;
        int resId = 0;
        switch (tab) {
            case GO_HOME:
                imageButtonActivitated = (ImageButton) rootView.findViewById(R.id.go_home);
                resId = R.drawable.go_home_activated;
                break;
            case GENUINES:
                imageButtonActivitated = (ImageButton) rootView.findViewById(R.id.genuines);
                resId = R.drawable.the_genuine_activated;
                break;
            case AROUND:
                imageButtonActivitated = (ImageButton) rootView.findViewById(R.id.around);
                resId = R.drawable.around_activated;
                break;
            case FAVOR:
                imageButtonActivitated = (ImageButton) rootView.findViewById(R.id.favor);
                resId = R.drawable.favor_activated;
                break;
            case ACCOUNT:
                imageButtonActivitated = (ImageButton) rootView.findViewById(R.id.account);
                resId = R.drawable.account_activated;
                break;
            default:
                break;
        }
        if (imageButtonActivitated != null) {
            imageButtonActivitated.setImageResource(resId);
        }
    }
}
