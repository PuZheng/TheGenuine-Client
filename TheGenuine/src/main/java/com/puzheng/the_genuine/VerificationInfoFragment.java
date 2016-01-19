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
import com.puzheng.the_genuine.model.VerificationInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xc on 13-11-21.
 */
public class VerificationInfoFragment extends Fragment {
    private VerificationInfo verificationInfo;
    private View rootView;

    public VerificationInfoFragment() {

    }

    public VerificationInfoFragment setVerificationInfo(VerificationInfo verificationInfo) {
        this.verificationInfo = verificationInfo;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_verification_info, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.textViewCode);
        textView.setText(verificationInfo.getSKU().getSPU().getCode());
        textView = (TextView) rootView.findViewById(R.id.textViewName);
        textView.setText(verificationInfo.getSKU().getSPU().getName());
        textView = (TextView) rootView.findViewById(R.id.textViewVendorName);
        textView.setText(verificationInfo.getSKU().getSPU().getVendorName());
        textView = (TextView) rootView.findViewById(R.id.textViewManufactureDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        textView.setText(simpleDateFormat.format(verificationInfo.getSKU().getManufactureDate()));
        textView = (TextView) rootView.findViewById(R.id.textViewExpiredDate);
        textView.setText(simpleDateFormat.format(verificationInfo.getSKU().getExpireDate()));

        Date now = new Date();
        // expried
        if (now.after(verificationInfo.getSKU().getExpireDate())) {
            textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        textView = (TextView) rootView.findViewById(R.id.verifyCnt);
        textView.setText(String.valueOf(verificationInfo.getVerifyCnt()));

        textView = (TextView) rootView.findViewById(R.id.lastVerifyTime);
        simpleDateFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
        Date lastVerifyDate = verificationInfo.getLastVerifyTime();
        textView.setText(lastVerifyDate != null ? simpleDateFormat.format(lastVerifyDate) : "--");

        textView = (TextView) rootView.findViewById(R.id.textViewVendorAddress);
        textView.setText(verificationInfo.getSKU().getSPU().getVendor().getAddress());

        textView = (TextView) rootView.findViewById(R.id.textViewVendorWebsite);
        final String website = verificationInfo.getSKU().getSPU().getVendor().getWebsite();
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

        final String telephone = verificationInfo.getSKU().getSPU().getVendor().getTel();
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
