package com.puzheng.the_genuine.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.puzheng.the_genuine.netutils.WebService;

/**
 * Created by xc on 13-11-22.
 */
public class GetImageTask extends AsyncTask<Integer, Void, Bitmap> {
    private static final int LARGE_SAMPLE_SIZE = 1;
    private final ImageView mImageView;
    private final String mUrl;
    private final String mKey;
    private final ImageCache mImageCache;
    private Exception ex;

    public GetImageTask(ImageView imageView, String picUrl) {
        this.mImageView = imageView;
        this.mUrl = picUrl;
        this.mKey = Misc.getMd5Hash(picUrl);
        mImageCache = ImageCache.getInstance(mImageView.getContext());
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        mImageView.setTag(mUrl);
        if (TextUtils.isEmpty(mUrl)) {
            return null;
        }
        int sampleSize;
        try {
            sampleSize = params[0];
        } catch (IndexOutOfBoundsException e) {
            sampleSize = LARGE_SAMPLE_SIZE;
        }
        try {
            Bitmap bitmap = mImageCache.getBitmapFromDiskCache(mKey, sampleSize);
            if (bitmap == null) {
                mImageCache.addBitmapToCache(mKey, WebService.getInstance(mImageView.getContext()).getStreamFromUrl(mUrl));
                return mImageCache.getBitmapFromDiskCache(mKey, sampleSize);
            }
            return bitmap;
        } catch (Exception e) {
            ex = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mImageView.getTag() != mUrl) {
            return;
        }
        Context context = mImageView.getContext();
        //如果activity已经关闭，不需要再设置
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }

        if (ex == null && bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        } else {
            mImageView.setImageBitmap(null);
        }
    }
}
