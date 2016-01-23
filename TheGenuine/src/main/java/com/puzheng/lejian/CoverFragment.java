package com.puzheng.lejian;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.puzheng.lejian.image_utils.ImageWorker;

/**
 * Created by xc on 13-11-23.
 */
public class CoverFragment extends Fragment {
    private String url;
    private ImageView imageView;

    public CoverFragment() {

    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imageView = (ImageView) inflater.inflate(R.layout.fragment_cover, container, false);
        Glide.with(getActivity()).load(url).into(imageView);
        return imageView;
    }

}
