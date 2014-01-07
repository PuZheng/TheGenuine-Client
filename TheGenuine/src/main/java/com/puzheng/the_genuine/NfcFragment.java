package com.puzheng.the_genuine;


import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-05.
 */
public class NfcFragment extends Fragment {
    private boolean reset;
    private Button enableNFCButton;
    private View mask;
    private NfcAdapter mNfcAdapter;
    private TextView textView;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{new String[]{NfcA.class.getName()}};
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this.getActivity());
        View rootView = inflater.inflate(R.layout.fragment_nfc, container, false);
        setHasOptionsMenu(true);
        enableNFCButton = (Button) rootView.findViewById(R.id.enableNFCButton);
        enableNFCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        textView = (TextView) rootView.findViewById(R.id.textView);
        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new BarCodeFragment());
                ft.commit();
            }
        });

        mask = rootView.findViewById(R.id.mask);
        return rootView;
    }

    @Override
    public void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this.getActivity(), mNfcAdapter);
        super.onPause();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onResume() {
        super.onResume();
        if (!mNfcAdapter.isEnabled()) {
            textView.setVisibility(View.GONE);
            enableNFCButton.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
            enableNFCButton.setVisibility(View.GONE);
            startAnim();
            /**
             * It's important, that the activity is in the foreground (resumed). Otherwise
             * an IllegalStateException is thrown.
             */
            setupForegroundDispatch(this.getActivity(), mNfcAdapter);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void startAnim() {
        //TODO this should be adapt to older versions
        final float scale = getResources().getDisplayMetrics().density;
        final ViewPropertyAnimator anim = mask.animate().translationY(scale * 100).withLayer().setDuration(3000);
        reset = true;
        anim.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (reset) {
                    anim.translationY(0).withLayer().setDuration(0);
                } else {
                    anim.translationY(scale * 100).withLayer().setDuration(3000);
                }
                reset = (!reset);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationStart(Animator animation) {

            }
        });
    }

}
