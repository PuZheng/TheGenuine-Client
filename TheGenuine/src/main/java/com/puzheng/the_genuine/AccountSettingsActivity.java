package com.puzheng.the_genuine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.puzheng.the_genuine.views.NavBar;

import java.io.Serializable;
import java.util.HashMap;

public class AccountSettingsActivity extends Activity implements  BackPressedInterface{

    private BackPressedHandle mBackPressedHandle = new BackPressedHandle();

    @Override
    public void doBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_settings, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MyApp.getCurrentUser() == null) {
            login();
        }

        setContentView(R.layout.activity_account_settings);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.unsetCurrentUser();
                login();
            }
        });
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);
    }

    private void login() {
        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("ISTOPACTIVITY", true);
        MyApp.doLoginIn(AccountSettingsActivity.this, map);
    }

    @Override
    public void onBackPressed() {
        mBackPressedHandle.doBackPressed(this, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            this.finish();
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApp.LOGIN_ACTION) {
            }
        }
    }
}
