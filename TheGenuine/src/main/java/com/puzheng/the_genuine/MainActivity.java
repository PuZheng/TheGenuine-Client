package com.puzheng.the_genuine;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.puzheng.the_genuine.data_structure.VerificationInfo;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.PoliteBackgroundTask;

public class MainActivity extends Activity {

    public static final String TAG_VERIFICATION_INFO = "VERIFICATION_INFO";

    private NfcAdapter mNfcAdapter;
    private Button enableNFCButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //TODO temporarily closed
        /*
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        */
        enableNFCButton = (Button) findViewById(R.id.enableNFCButton);
        enableNFCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        handleIntent(getIntent());
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled()) {
                enableNFCButton.setVisibility(View.VISIBLE);
            } else {
                enableNFCButton.setVisibility(View.GONE);
                /**
                 * It's important, that the activity is in the foreground (resumed). Otherwise
                 * an IllegalStateException is thrown.
                 */
                setupForegroundDispatch(this, mNfcAdapter);
            }
        }
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        if (mNfcAdapter != null) {
            stopForegroundDispatch(this, mNfcAdapter);
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{new String[]{NfcA.class.getName()}};
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
/*
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
*/
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        final String code = extractNFCMessage();

        PoliteBackgroundTask.Builder<VerificationInfo> builder = new PoliteBackgroundTask.Builder<VerificationInfo>(this);
        builder.msg("已读取NFC信息，正在验证真伪");
        builder.run(new PoliteBackgroundTask.XRunnable<VerificationInfo>() {
            @Override
            public VerificationInfo run() throws Exception {
                return WebService.getInstance(MainActivity.this).verify(code);
            }
        });
        builder.after(new PoliteBackgroundTask.OnAfter<VerificationInfo>() {


            @Override
            public void onAfter(VerificationInfo verificationInfo) {
                Intent intent;
                if (verificationInfo != null) {
                    intent = new Intent(MainActivity.this, ProductActivity.class);
                    intent.putExtra(TAG_VERIFICATION_INFO, verificationInfo);
                } else {
                    intent = new Intent(MainActivity.this, CounterfeitActivity.class);
                }
                startActivity(intent);
            }
        });
        builder.create().start();
    }

    private String extractNFCMessage() {
        //TODO unimplmented
        return "foo";
                /*
        if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) ||
                (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)))
        {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Parcelable[] rawMsgs = intent
                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            byte[] data = msg.getRecords()[0].getPayload();
            StringBuilder sb = new StringBuilder(data.length);
            for (int i = 0; i < data.length; ++ i) {
                if (data[i] < 0) throw new IllegalArgumentException();
                sb.append((char) data[i]);
            }
            String displaystring = sb.toString();
        }
        */
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
