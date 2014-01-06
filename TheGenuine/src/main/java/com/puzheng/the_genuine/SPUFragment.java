package com.puzheng.the_genuine;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.puzheng.the_genuine.data_structure.SPU;

/**
 * Created by xc on 13-12-4.
 */
public class SPUFragment extends Fragment {
    private final SPU spu;

    public SPUFragment(SPU spu) {
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
        textView = (TextView) rootView.findViewById(R.id.textViewVendorAddress);
        textView.setText(spu.getVendor().getAddress());

        textView = (TextView) rootView.findViewById(R.id.textViewVendorWebsite);
        final String website = spu.getVendor().getWebsite();
        textView.setText(spu.getVendor().getWebsite());
        if (!TextUtils.isEmpty(website)) {
            textView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
            textView.setTextColor(Color.BLUE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    getActivity().startActivity(Intent.createChooser(browserIntent, "选择浏览器"));
                }
            });
        }

        final String telephone = spu.getVendor().getTel();
        textView = (TextView) rootView.findViewById(R.id.textViewVendorTel);
        textView.setText(telephone);
        if (!TextUtils.isEmpty(telephone)) {
            rootView.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephone));
                    startActivity(callIntent);
                }
            });
        }
        return rootView;
    }
}
