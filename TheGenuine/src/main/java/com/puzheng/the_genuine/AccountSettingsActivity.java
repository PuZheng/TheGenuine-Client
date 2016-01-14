package com.puzheng.the_genuine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.puzheng.the_genuine.data_structure.User;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.db.OauthHelper;

import java.io.Serializable;
import java.util.HashMap;

public class AccountSettingsActivity extends Activity implements BackPressedInterface {

    private BackPressedHandle mBackPressedHandle = new BackPressedHandle();
    private UMSocialService mController;
    private Button cancelButton;

    @Override
    public void doBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        mBackPressedHandle.doBackPressed(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_settings, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            this.finish();
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApp.LOGIN_ACTION) {
                setUsername();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mController = UMServiceFactory.getUMSocialService("com.umeng.login",
                RequestType.SOCIAL);
        super.onCreate(savedInstanceState);
        if (MyApp.getCurrentUser() == null) {
            login();
        }

        setContentView(R.layout.activity_account_settings);

        setUsername();

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.unsetCurrentUser();
                login();
            }
        });
//        mController.openUserCenter(AccountSettingsActivity.this, SocializeConstants.FLAG_USER_CENTER_HIDE_LOGININFO);
        cancelButton = (Button) findViewById(R.id.cancelVerify);
        if (OauthHelper.isAuthenticated(AccountSettingsActivity.this, SHARE_MEDIA.SINA)) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mController.deleteOauth(AccountSettingsActivity.this, SHARE_MEDIA.SINA, new SocializeListeners.SocializeClientListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onComplete(int i, SocializeEntity socializeEntity) {
                            if (i == 200 && socializeEntity != null) {
                                Toast.makeText(AccountSettingsActivity.this, getString(R.string.unbinding_succeed), Toast.LENGTH_SHORT).show();
                                cancelButton.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(AccountSettingsActivity.this, getString(R.string.unbinding_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            cancelButton.setVisibility(View.GONE);
        }
    }

    private void login() {
        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("ISTOPACTIVITY", true);
        MyApp.doLoginIn(AccountSettingsActivity.this, map);
    }

    private void setUsername() {
        User user = MyApp.getCurrentUser();
        TextView textView = (TextView) findViewById(R.id.textViewEmail);
        if (user != null) {
            textView.setText(user.getEmail());
        } else {
            textView.setText("");
        }
    }
}
