package com.puzheng.lejian;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.lejian.model.Denounce;
import com.puzheng.lejian.store.DenounceStore;
import com.puzheng.lejian.store.LocationStore;
import com.puzheng.lejian.util.Misc;

public class WarningActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_warning);
        token = getIntent().getStringExtra(AuthenticationActivity.TAG_TOKEN);

        Button button = (Button) findViewById(R.id.btnNearby);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WarningActivity.this, NearbyActivity.class);
                startActivity(intent);
            }
        });

        button = (Button) findViewById(R.id.denounce);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WarningActivity.this);
                builder.setTitle(R.string.counterfeit);
                final EditText editText = new EditText(WarningActivity.this);
                editText.setHint(R.string.enter_reason);
                editText.setMinLines(3);
                editText.setGravity(Gravity.TOP);
                builder.setView(editText);

                progressDialog = new ProgressDialog(WarningActivity.this);
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String reason = editText.getText().toString();
                        if (TextUtils.isEmpty(reason)) {
                            Toast.makeText(WarningActivity.this, R.string.denounce_empty, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                        progressDialog.show();

                        LocationStore.getInstance().getLocation().done(new DoneHandler<Pair<Double, Double>>() {
                            @Override
                            public void done(Pair<Double, Double> lnglat) {
                                DenounceStore.getInstance().denounce(token, reason, lnglat).done(new DoneHandler<Denounce>() {
                                    @Override
                                    public void done(Denounce denounce) {
                                        Toast.makeText(WarningActivity.this, R.string.denounce_success, Toast.LENGTH_SHORT).show();
                                    }
                                }).fail(new FailHandler<Void>() {
                                    @Override
                                    public void fail(Void aVoid) {
                                        Toast.makeText(WarningActivity.this, R.string.denounce_fail, Toast.LENGTH_SHORT).show();
                                    }
                                }).always(new AlwaysHandler() {
                                    @Override
                                    public void always() {
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
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
            if (TextUtils.isEmpty(token)) {
                return false;
            }
//            try {
//                return WebService.getInstance(WarningActivity.this).denounce(token, params[0]);
//            } catch (Exception e) {
//                exception = e;
//            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b) {
                Toast.makeText(WarningActivity.this, R.string.denounce_success, Toast.LENGTH_SHORT).show();
            } else {
                if (Misc.isNetworkException(exception)) {
                    Toast.makeText(WarningActivity.this, R.string.httpError, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WarningActivity.this, R.string.denounce_fail, Toast.LENGTH_SHORT).show();
                }
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.warning, menu);
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
