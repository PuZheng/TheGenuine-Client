package com.puzheng.the_genuine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.puzheng.the_genuine.image_utils.ImageWorker;

/**
 * Created by xc on 13-11-23.
 */
public class CoverFragment extends Fragment {
    private final String url;
    private ImageFetcherInteface mImageFetcherInteface;
    private ImageView imageView;

    public CoverFragment(Context context, String url, ImageFetcherInteface imageFetcherInteface) {
        this.url = url;
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
