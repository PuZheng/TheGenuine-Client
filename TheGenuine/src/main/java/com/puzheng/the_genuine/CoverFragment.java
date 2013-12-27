package com.puzheng.the_genuine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.puzheng.the_genuine.image_utils.ImageFetcher;

/**
 * Created by xc on 13-11-23.
 */
public class CoverFragment extends Fragment {
    private final String url;
    private static volatile ImageFetcher mImageFetcher;


    public CoverFragment(Context context, String url) {
        this.url = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mImageFetcher == null) {
            mImageFetcher = ImageFetcher.getImageFetcher(this.getActivity(), Integer.MAX_VALUE, 0.25f);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ImageView imageView = (ImageView) inflater.inflate(R.layout.fragment_cover, container, false);
        mImageFetcher.loadImage(url, imageView);
        return imageView;
    }
}
