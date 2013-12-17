package com.puzheng.the_genuine;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import com.puzheng.the_genuine.views.NavBar;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-17.
 */
public class RegisterActivity extends ActionBarActivity {
    public static final int TAG_REGISTER = 2;

    public void showRegisterFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main, new RegisterFragment());
        ft.commit();
    }

    /**
     * Attempts to sign in or register the account specified by the register_or_login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual register_or_login attempt is made.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        showRegisterFragment();

        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
