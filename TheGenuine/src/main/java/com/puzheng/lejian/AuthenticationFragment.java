package com.puzheng.lejian;

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
import com.puzheng.lejian.model.Verification;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xc on 13-11-21.
 */
public class AuthenticationFragment extends Fragment {
    private Verification verification;
    private View rootView;

    public AuthenticationFragment() {

    }

    public AuthenticationFragment setVerificationInfo(Verification verification) {
        this.verification = verification;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_verification_info, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.textViewCode);
        textView.setText(verification.getSKU().getSPU().getCode());
        textView = (TextView) rootView.findViewById(R.id.textViewName);
        textView.setText(verification.getSKU().getSPU().getName());
        textView = (TextView) rootView.findViewById(R.id.textViewVendorName);
        textView.setText(verification.getSKU().getSPU().getVendorName());
        textView = (TextView) rootView.findViewById(R.id.textViewManufactureDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Const.DATE_FORMAT);
        textView.setText(simpleDateFormat.format(verification.getSKU().getManufactureDate()));
        textView = (TextView) rootView.findViewById(R.id.textViewExpiredDate);
        textView.setText(simpleDateFormat.format(verification.getSKU().getExpireDate()));

        Date now = new Date();
        // expried
        if (now.after(verification.getSKU().getExpireDate())) {
            textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        textView = (TextView) rootView.findViewById(R.id.verifyCnt);
        textView.setText(String.valueOf(verification.getVerifyCnt()));

        textView = (TextView) rootView.findViewById(R.id.lastVerifyTime);
        simpleDateFormat = new SimpleDateFormat(Const.TIME_FORMAT);
        Date lastVerifyDate = verification.getLastVerifyTime();
        textView.setText(lastVerifyDate != null ? simpleDateFormat.format(lastVerifyDate) : "--");

        textView = (TextView) rootView.findViewById(R.id.textViewVendorAddress);
        textView.setText(verification.getSKU().getSPU().getVendor().getAddr());

        textView = (TextView) rootView.findViewById(R.id.textViewVendorWebsite);
        final String website = verification.getSKU().getSPU().getVendor().getWebsite();
        textView.setText(website);
        if (!TextUtils.isEmpty(website)) {
            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            textView.setTextColor(Color.BLUE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    getActivity().startActivity(Intent.createChooser(browserIntent, "选择浏览器"));
                }
            });
        }

        final String telephone = verification.getSKU().getSPU().getVendor().getTel();
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
