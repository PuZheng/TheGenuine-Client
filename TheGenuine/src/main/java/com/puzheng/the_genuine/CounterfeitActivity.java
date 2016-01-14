package com.puzheng.the_genuine;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.util.Misc;

public class CounterfeitActivity extends Activity {
    private ProgressDialog progressDialog;
    private String mTag ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        setContentView(R.layout.activity_counterfeit);
        mTag = getIntent().getStringExtra(MainActivity.TAG_TAG_ID);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterfeitActivity.this, CategoriesActivity.class);
                startActivity(intent);
            }
        });

        Button b = (Button) findViewById(R.id.denounce);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CounterfeitActivity.this);
                builder.setTitle(R.string.counterfeit);
                final EditText reason = new EditText(CounterfeitActivity.this);
                reason.setHint(R.string.enter_reason);
                reason.setMinLines(3);
                reason.setGravity(Gravity.TOP);
                builder.setView(reason);

                progressDialog = new ProgressDialog(CounterfeitActivity.this);
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reasonStr = reason.getText().toString();
                        if (TextUtils.isEmpty(reasonStr)) {
                            Toast.makeText(CounterfeitActivity.this, R.string.denounce_empty, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                        progressDialog.show();
                        new DenounceTask().execute(reasonStr);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                dialog.show();
            }
        });

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
        MediaPlayer mMediaPlayer = MediaPlayer.create(this, R.raw.failure);
        mMediaPlayer.setLooping(false);
        mMediaPlayer.start();

    }

    class DenounceTask extends AsyncTask<String, Void, Boolean> {
        private Exception exception;
        @Override
        protected Boolean doInBackground(String... params) {
            if (TextUtils.isEmpty(mTag)) {
                return false;
            }
            try {
                return WebService.getInstance(CounterfeitActivity.this).denounce(mTag, params[0]);
            } catch (Exception e) {
                exception = e;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b) {
                Toast.makeText(CounterfeitActivity.this, R.string.denounce_success, Toast.LENGTH_SHORT).show();
            } else {
                if (Misc.isNetworkException(exception)) {
                    Toast.makeText(CounterfeitActivity.this, R.string.httpError, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CounterfeitActivity.this, R.string.denounce_fail, Toast.LENGTH_SHORT).show();
                }
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.counterfeit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
