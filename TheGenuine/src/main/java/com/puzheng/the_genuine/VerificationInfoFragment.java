package com.puzheng.the_genuine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puzheng.the_genuine.data_structure.VerificationInfo;

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
        return rootView;
    }

    public static Fragment getInstance(Context context, VerificationInfo verificationInfo) {
        if (instance == null) {
            instance = new VerificationInfoFragment(context, verificationInfo);
        }
        return instance;
    }
}
