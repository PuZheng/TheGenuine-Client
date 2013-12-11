package com.puzheng.the_genuine.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.puzheng.the_genuine.*;

/**
 * Created by xc on 13-11-26.
 */
public class NavBar extends LinearLayout {
    private static final int GO_HOME = 0;
    private static final int GENUINES = 1;
    private static final int AROUND = 2;
    public static final int FAVOR = 3;
    private static final int ACCOUNT = 4;
    private final View rootView;
    private Context context;
    private int mCurrentActiveTabId;

    public NavBar(final Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.nav_bar, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.NavBar, 0, 0);
        int tab = a.getInteger(R.styleable.NavBar_enabledTab, GO_HOME);
        enableTab(tab, false);

        initTab(R.id.go_home, MainActivity.class);
        initTab(R.id.genuines, CategoriesActivity.class);
        initTab(R.id.nearby, NearbyActivity.class);
        initTab(R.id.favor, FavorCategoriesActivity.class);
        initTab(R.id.account, AccountSettingsActivity.class);
    }

    private void initTab(final int resId, final Class<?> activityClass) {
        ImageButton imageButton = (ImageButton) findViewById(resId);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentActiveTabId == resId) {
                    return;
                }
                Intent intent = new Intent(context, activityClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void enableTab(int tab, boolean disableOthers) {
        if (disableOthers) {
            ImageButton imageButton = (ImageButton) findViewById(R.id.go_home);
            imageButton.setImageResource(R.drawable.home);
            imageButton = (ImageButton) findViewById(R.id.genuines);
            imageButton.setImageResource(R.drawable.the_genuine);
            imageButton = (ImageButton) findViewById(R.id.nearby);
            imageButton.setImageResource(R.drawable.around);
            imageButton = (ImageButton) findViewById(R.id.favor);
            imageButton.setImageResource(R.drawable.favor);
            imageButton = (ImageButton) findViewById(R.id.account);
            imageButton.setImageResource(R.drawable.account);
        }
        ImageButton imageButtonActivitated = null;
        int resId = 0;
        switch (tab) {
            case GO_HOME:
                mCurrentActiveTabId = R.id.go_home;
                imageButtonActivitated = (ImageButton) rootView.findViewById(R.id.go_home);
                resId = R.drawable.go_home_activated;
                break;
            case GENUINES:
                mCurrentActiveTabId = R.id.genuines;
                imageButtonActivitated = (ImageButton) rootView.findViewById(R.id.genuines);
                resId = R.drawable.the_genuine_activated;
                break;
            case AROUND:
                mCurrentActiveTabId = R.id.nearby;
                imageButtonActivitated = (ImageButton) rootView.findViewById(R.id.nearby);
                resId = R.drawable.around_activated;
                break;
            case FAVOR:
                mCurrentActiveTabId = R.id.favor;
                imageButtonActivitated = (ImageButton) rootView.findViewById(R.id.favor);
                resId = R.drawable.favor_activated;
                break;
            case ACCOUNT:
                mCurrentActiveTabId = R.id.account;
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
