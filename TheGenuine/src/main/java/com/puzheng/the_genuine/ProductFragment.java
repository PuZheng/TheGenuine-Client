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
public class ProductFragment extends Fragment {
    private static ProductFragment instance;
    private final Context context;
    private final SPU SPU;

    public ProductFragment(Context context, SPU SPU) {
        this.context = context;
        this.SPU = SPU;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.textViewCode);
        textView.setText(SPU.getCode());
        textView = (TextView) rootView.findViewById(R.id.textViewName);
        textView.setText(SPU.getName());
        textView = (TextView) rootView.findViewById(R.id.textViewVendorName);
        textView.setText(SPU.getVendorName());
        return rootView;
    }

    public static Fragment getInstance(Context context, SPU SPU) {
        if (instance == null) {
            instance = new ProductFragment(context, SPU);
        }
        return instance;
    }
}
