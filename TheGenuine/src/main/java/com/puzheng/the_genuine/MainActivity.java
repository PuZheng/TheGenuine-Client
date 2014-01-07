package com.puzheng.the_genuine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;
import com.puzheng.the_genuine.data_structure.VerificationInfo;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.Misc;
import com.puzheng.the_genuine.utils.PoliteBackgroundTask;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MainActivity extends Activity implements BackPressedInterface {
    public static final String TAG_VERIFICATION_INFO = "VERIFICATION_INFO";
    public static final String TAG_TAG_ID = "TOKEN_ID";
    public static final String TAG_PRODUCT_RESPONSE = "PRODUCT_RESPONSE";
    private static final String MIME_TEXT_PLAIN = "text/plain";
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    public static boolean isNfcEnabled = true;


    @Override
    public void doBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        backPressedHandle.doBackPressed(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        isNfcEnabled = NfcAdapter.getDefaultAdapter(this) != null;
        if (isNfcEnabled) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container, new NfcFragment());
            ft.commit();
            handleIntent(getIntent());
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container, new BarCodeFragment());
            ft.commit();
        }

        if (!isNetworkAvailable()) {
            setNetwork();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    private void setNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("网络设置").setMessage("网络连接不可用, 是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                startActivity(intent);
            }
        }).setNegativeButton("取消", null).show();
    }

    private String extractCode(Intent intent) {
        String action = intent.getAction();
        Tag tag = null;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            } else {
                Toast.makeText(MainActivity.this, "Wrong mime type: " + type, Toast.LENGTH_SHORT).show();
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();
            boolean hit = false;
            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                Toast.makeText(MainActivity.this, "无法识别的NFC标签", Toast.LENGTH_SHORT).show();
                tag = null;
            }
        }
        if (tag == null) {
            return null;
        }
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }
        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        if (ndefMessage != null) {
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                    if (Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                        try {
                            return readText(ndefRecord);
                        } catch (UnsupportedEncodingException e) {
                            Toast.makeText(MainActivity.this, "Unsupported Encoding" + e, Toast.LENGTH_SHORT).show();
                        }
                    } else if (Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI)) {
                        return readURI(ndefRecord);
                    }
                }
            }
        } else {
            Toast.makeText(MainActivity.this, "无法识别的NFC标签", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void handleIntent(Intent intent) {
        final String code = extractCode(intent);
        if (!TextUtils.isEmpty(code)) {
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
                        intent = new Intent(MainActivity.this, SPUActivity.class);
                        intent.putExtra(TAG_VERIFICATION_INFO, verificationInfo);
                    } else {
                        intent = new Intent(MainActivity.this, CounterfeitActivity.class);
                        intent.putExtra(TAG_TAG_ID, code);
                    }
                    startActivity(intent);
                }
            });
            builder.exceptionHandler(new PoliteBackgroundTask.ExceptionHandler() {
                @Override
                public void run(Exception e) {
                    if (Misc.isNetworkException(e)) {
                        Toast.makeText(MainActivity.this, R.string.httpError, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    MainActivity.this.onResume();
                }
            });
            builder.create().start();
        }
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */
        byte[] payload = record.getPayload();
        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"
        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    private String readURI(NdefRecord record) {
        /*
         * see NFC Data Exchange Format (NDEF) Technical Specification URI TYPE DEFINITION
         */
        byte[] payload = record.getPayload();
        byte identifierCode = payload[0];
        // http://www or https://www or http:// or https://
        if (identifierCode == 1 || identifierCode == 2 || identifierCode == 3 || identifierCode == 4) {
            // compensate "http://"
            return "http://" + new String(payload, 1, payload.length-1);
        }
        return null;
    }
}
