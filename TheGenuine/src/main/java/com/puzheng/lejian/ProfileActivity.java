package com.puzheng.lejian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.puzheng.lejian.store.AuthStore;

import java.io.Serializable;
import java.util.HashMap;

public class ProfileActivity extends Activity implements BackPressedInterface {

    private BackPressedHandle mBackPressedHandle = new BackPressedHandle();
//    private UMSocialService service;
    private Button unbindButton;

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
                ((TextView) findViewById(R.id.textViewEmail)).setText(
                        AuthStore.getInstance().getUser().getEmail());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (AuthStore.getInstance().getUser() == null) {
            login();
            return;
        }

        setContentView(R.layout.activity_profile);

        ((TextView) findViewById(R.id.textViewEmail)).setText(
                AuthStore.getInstance().getUser().getEmail());

        Button button = (Button) findViewById(R.id.btnNearby);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthStore.getInstance().logout();
                login();
            }
        });
//        service.openUserCenter(ProfileActivity.this, SocializeConstants.FLAG_USER_CENTER_HIDE_LOGININFO);
//        service = UMServiceFactory.getUMSocialService("com.umeng.login",
//                RequestType.SOCIAL);
//        unbindButton = (Button) findViewById(R.id.unbindWeibo);
//        if (OauthHelper.isAuthenticated(ProfileActivity.this, SHARE_MEDIA.SINA)) {
//            unbindButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    service.deleteOauth(ProfileActivity.this, SHARE_MEDIA.SINA, new SocializeListeners.SocializeClientListener() {
//                        @Override
//                        public void onStart() {
//
//                        }
//
//                        @Override
//                        public void onComplete(int i, SocializeEntity socializeEntity) {
//                            if (i == 200 && socializeEntity != null) {
//                                Toast.makeText(ProfileActivity.this, getString(R.string.unbinding_succeed), Toast.LENGTH_SHORT).show();
//                                unbindButton.setVisibility(View.GONE);
//                            } else {
//                                Toast.makeText(ProfileActivity.this, getString(R.string.unbinding_failed), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            });
//        } else {
//            unbindButton.setVisibility(View.GONE);
//        }
    }

    private void login() {
        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("ISTOPACTIVITY", true);
        MyApp.doLoginIn(ProfileActivity.this, map);
    }

}
