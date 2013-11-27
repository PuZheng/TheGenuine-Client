package com.puzheng.the_genuine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.puzheng.the_genuine.data_structure.VerificationInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xc on 13-11-21.
 */
public class VerificationInfoFragment extends Fragment {
    private static VerificationInfoFragment instance;
    private final Context context;
    private final VerificationInfo verificationInfo;
    private View rootView;

    public VerificationInfoFragment(Context context, VerificationInfo verificationInfo) {
        this.context = context;
        this.verificationInfo = verificationInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_verification_info, container, false);
        TextView textView = (TextView)rootView.findViewById(R.id.textViewCode);
        textView.setText(verificationInfo.getCode());
        textView = (TextView)rootView.findViewById(R.id.textViewName);
        textView.setText(verificationInfo.getName());
        textView = (TextView)rootView.findViewById(R.id.textViewVendorName);
        textView.setText(verificationInfo.getVendorName());
        textView = (TextView)rootView.findViewById(R.id.textViewManufactureDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        textView.setText(simpleDateFormat.format(verificationInfo.getManufactureDate()));
        textView = (TextView)rootView.findViewById(R.id.textViewExpiredDate);
        textView.setText(simpleDateFormat.format(verificationInfo.getExpiredDate()));

        Date now = new Date();
        // expried
        if (now.after(verificationInfo.getExpiredDate())) {
            textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
        return rootView;
    }

    public static Fragment getInstance(Context context, VerificationInfo verificationInfo) {
        if (instance == null) {
            instance = new VerificationInfoFragment(context, verificationInfo);
        }
        return instance;
    }
}
