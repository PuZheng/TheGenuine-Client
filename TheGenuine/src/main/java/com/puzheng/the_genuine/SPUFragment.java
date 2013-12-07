package com.puzheng.the_genuine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.puzheng.the_genuine.data_structure.SPU;

/**
 * Created by xc on 13-12-4.
 */
public class SPUFragment extends Fragment {
    private static SPUFragment instance;
    private final Context context;
    private final SPU spu;

    public SPUFragment(Context context, SPU spu) {
        this.context = context;
        this.spu = spu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.textViewCode);
        textView.setText(spu.getCode());
        textView = (TextView) rootView.findViewById(R.id.textViewName);
        textView.setText(spu.getName());
        textView = (TextView) rootView.findViewById(R.id.textViewVendorName);
        textView.setText(spu.getVendorName());
        return rootView;
    }

    public static Fragment getInstance(Context context, SPU SPU) {
        if (instance == null) {
            instance = new SPUFragment(context, SPU);
        }
        return instance;
    }
}
