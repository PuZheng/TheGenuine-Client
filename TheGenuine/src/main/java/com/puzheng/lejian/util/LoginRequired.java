package com.puzheng.lejian.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.puzheng.lejian.LoginActivity;
import com.puzheng.lejian.model.User;
import com.puzheng.lejian.store.AuthStore;

public class LoginRequired {

    private static final int LOGIN_ACTION = 1;
    private final Context context;

    private LoginRequired(Context context) {
        this.context = context;
    }

    public static LoginRequired with(Context context) {
        return new LoginRequired(context);
    }

    public void wraps(Runnable runnable) {
        if (AuthStore.getInstance().isAnonymous()) {
            Intent intent = new Intent(context, LoginActivity.class);
            ((Activity) context).startActivityForResult(intent, LOGIN_ACTION);
            ((ILoginHandler) context).onLoginDone(runnable);
        } else {
            runnable.run(AuthStore.getInstance().getUser());
        }
    }

    public interface Runnable {
        void run(User user);
    }

    public interface ILoginHandler {
        void onLoginDone(Runnable runnable);
    }


    public static class LoginHandler implements ILoginHandler {
        private Runnable runnable;

        public LoginHandler() {

        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == LOGIN_ACTION) {
                    runnable.run(AuthStore.getInstance().getUser());
                }
            }
        }

        public void onLoginDone(Runnable runnable) {
            this.runnable = runnable;
        }
    }
}
