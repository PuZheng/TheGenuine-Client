package com.puzheng.the_genuine;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.puzheng.the_genuine.utils.BadResponseException;
import com.puzheng.the_genuine.utils.LocateErrorException;
import com.puzheng.the_genuine.utils.Misc;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-16.
 */
public class MaskableManager {
    private View targetView;
    private RefreshInterface mRefreshInterface;
    private View mRootView;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private ImageButton mImageButton;


    public MaskableManager(View view, RefreshInterface i) {
        this.targetView = view;
        this.mRefreshInterface = i;
        this.mRootView = getMaskView(view);
    }

    public void mask() {
        if (mRootView.getParent() == null) {
            ViewGroup parent = (ViewGroup) targetView.getParent();
            parent.removeView(targetView);
            parent.addView(mRootView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.GONE);
        mImageButton.setVisibility(View.GONE);
    }

    /**
     * @param exception
     * @return 如果没有发生异常，则还原view，并且返回true
     */
    public boolean unmask(Exception exception) {
        if (exception == null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            parent.removeView(mRootView);
            parent.addView(targetView);
            return true;
        } else {
            if (BuildConfig.DEBUG) {
                if (exception instanceof BadResponseException) {
                    Log.e(MaskableManager.class.getSimpleName(), ((BadResponseException) exception).getUrl(), exception);
                } else {
                    Log.e(MaskableManager.class.getSimpleName(), String.valueOf(exception.getMessage()), exception);
                }
            }

            if (Misc.isNetworkException(exception)) {
                mImageButton.setImageResource(R.drawable.wifi_not_connected);
                mTextView.setText(R.string.httpErrorConnect);
            } else {
                mImageButton.setImageResource(R.drawable.ic_action_refresh);
                if ((exception instanceof BadResponseException || exception instanceof LocateErrorException)
                        && !TextUtils.isEmpty(exception.getMessage())) {
                    mTextView.setText(exception.getMessage());
                } else {
                    mTextView.setText(R.string.systemError);
                }
            }
            mProgressBar.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            mImageButton.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private View getMaskView(View view) {
        LinearLayout maskView = new LinearLayout(view.getContext());
        maskView.setOrientation(LinearLayout.VERTICAL);
        maskView.setGravity(Gravity.CENTER);

        mProgressBar = new ProgressBar(view.getContext(), null,
                android.R.attr.progressBarStyleLarge);
        maskView.addView(mProgressBar, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mImageButton = new ImageButton(view.getContext());
        mImageButton.setImageResource(R.drawable.wifi_not_connected);
        mImageButton.setBackgroundColor(view.getResources().getColor(android.R.color.transparent));
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshInterface.refresh();
            }
        });
        maskView.addView(mImageButton, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextView = new TextView(view.getContext());
        mTextView.setText(R.string.error_message);
        maskView.addView(mTextView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return maskView;
    }

}
