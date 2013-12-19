package com.puzheng.the_genuine;

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
    private final VerificationInfo verificationInfo;
    private View rootView;

    public VerificationInfoFragment(VerificationInfo verificationInfo) {
        this.verificationInfo = verificationInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_verification_info, container, false);
        TextView textView = (TextView)rootView.findViewById(R.id.textViewCode);
        textView.setText(verificationInfo.getSKU().getSPU().getCode());
        textView = (TextView)rootView.findViewById(R.id.textViewName);
        textView.setText(verificationInfo.getSKU().getSPU().getName());
        textView = (TextView)rootView.findViewById(R.id.textViewVendorName);
        textView.setText(verificationInfo.getSKU().getSPU().getVendorName());
        textView = (TextView)rootView.findViewById(R.id.textViewManufactureDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        textView.setText(simpleDateFormat.format(verificationInfo.getSKU().getManufactureDate()));
        textView = (TextView)rootView.findViewById(R.id.textViewExpiredDate);
        textView.setText(simpleDateFormat.format(verificationInfo.getSKU().getExpireDate()));

        Date now = new Date();
        // expried
        if (now.after(verificationInfo.getSKU().getExpireDate())) {
            textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
        return rootView;
    }
}
