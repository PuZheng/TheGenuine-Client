package com.puzheng.the_genuine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.puzheng.the_genuine.views.NavBar;

public class AccountSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.unsetCurrentUser();
                Intent intent = new Intent(AccountSettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_settings, menu);
        return true;
    }

}
