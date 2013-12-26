package com.puzheng.the_genuine.views;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import com.puzheng.the_genuine.R;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-25.
 */
public class CustomActionBar extends FrameLayout {
    private TextView mTitleView;
    private Activity mActivity;
    private TextView mSubTitleView;
    private ImageButton mImageButton;

    public CustomActionBar(Context context) {
        super(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.custom_action_bar, this, true);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(params);
        mTitleView = (TextView) rootView.findViewById(R.id.title);
        mSubTitleView = (TextView) rootView.findViewById(R.id.subTitle);
        mImageButton = (ImageButton) rootView.findViewById(R.id.upIndicator);
        if (mActivity != null) {
            mTitleView.setText(mActivity.getTitle());
        }
        setUpButtonEnable(false);
    }

    public static CustomActionBar setCustomerActionBar(ActionBar actionBar, Context context) {
        CustomActionBar customActionBar = new CustomActionBar(context);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        View homeIcon = ((Activity) context).findViewById(android.R.id.home);
        ((View) homeIcon.getParent()).setVisibility(View.GONE);
        homeIcon.setVisibility(GONE);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#219ca3")));
        actionBar.setCustomView(customActionBar);
        return customActionBar;
    }

//    public static CustomActionBar setCustomerActionBar(android.support.v7.app.ActionBar actionBar, Context context) {
//        CustomActionBar customActionBar = new CustomActionBar(context);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setHomeButtonEnabled(false);
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#219ca3")));
//        actionBar.setCustomView(customActionBar);
//        return customActionBar;
//    }

    public void setSubtitle(String subTilte) {
        mSubTitleView.setText(subTilte);
        mSubTitleView.setVisibility(VISIBLE);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setUpButtonEnable(Boolean enable) {
        if (enable) {
            mImageButton.setVisibility(VISIBLE);
            mImageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActivity != null) {
                        mActivity.onBackPressed();
                    }
                }
            });
            mTitleView.setGravity(Gravity.CENTER);
            mSubTitleView.setGravity(Gravity.CENTER);
        } else {
            mImageButton.setVisibility(GONE);
            mTitleView.setGravity(Gravity.CENTER_VERTICAL);
            mSubTitleView.setGravity(Gravity.CENTER_VERTICAL);
        }
    }


}
