package com.puzheng.the_genuine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.puzheng.the_genuine.data_structure.Product;

/**
 * Created by xc on 13-12-4.
 */
public class ProductFragment extends Fragment {
    private static ProductFragment instance;
    private final Context context;
    private final Product product;

    public ProductFragment(Context context, Product product) {
        this.context = context;
        this.product = product;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.textViewCode);
        textView.setText(product.getCode());
        textView = (TextView) rootView.findViewById(R.id.textViewName);
        textView.setText(product.getName());
        textView = (TextView) rootView.findViewById(R.id.textViewVendorName);
        textView.setText(product.getVendorName());
        return rootView;
    }

    public static Fragment getInstance(Context context, Product product) {
        if (instance == null) {
            instance = new ProductFragment(context, product);
        }
        return instance;
    }
}
