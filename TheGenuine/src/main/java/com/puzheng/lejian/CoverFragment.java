package com.puzheng.lejian;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.puzheng.lejian.image_utils.ImageWorker;

/**
 * Created by xc on 13-11-23.
 */
public class CoverFragment extends Fragment {
    private String url;
    private ImageFetcherInteface mImageFetcherInteface;
    private ImageView imageView;

    public CoverFragment() {

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setmImageFetcherInteface(ImageFetcherInteface imageFetcherInteface) {
        this.mImageFetcherInteface = imageFetcherInteface;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imageView = (ImageView) inflater.inflate(R.layout.fragment_cover, container, false);
        mImageFetcherInteface.getImageFetcher().loadImage(url, imageView);
        return imageView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageView != null) {
            ImageWorker.cancelWork(imageView);
            imageView.setImageDrawable(null);
        }
    }
}
