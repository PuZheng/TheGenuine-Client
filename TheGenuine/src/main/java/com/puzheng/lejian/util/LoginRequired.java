package com.puzheng.lejian.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.puzheng.lejian.LoginActivity;
import com.puzheng.lejian.model.User;
import com.puzheng.lejian.store.AuthStore;

public class LoginRequired {

    private static final int LOGIN_ACTION = 129384712;
    private final Context context;
    private int requestCode = LOGIN_ACTION;

    private LoginRequired(Context context) {
        this.context = context;
    }

    public static LoginRequired with(Context context) {
        return new LoginRequired(context);
    }

    public void wraps(Runnable runnable) {
        if (AuthStore.getInstance().isAnonymous()) {
            Intent intent = new Intent(context, LoginActivity.class);
            ((Activity) context).startActivityForResult(intent, requestCode);
            ((ILoginHandler) context).onLoginDone(runnable);
        } else {
            runnable.run(AuthStore.getInstance().getUser());
        }
    }

    public LoginRequired requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public LoginHandler createLoginHandler() {
        return new LoginHandler(this.requestCode);
    }

    public interface Runnable {
        void run(User user);
    }

    public interface ILoginHandler {
        void onLoginDone(Runnable runnable);
    }


    public static class LoginHandler implements ILoginHandler {
        private final int requestCode;
        private Runnable runnable;

        public LoginHandler(int requestCode) {
            this.requestCode = requestCode;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == this.requestCode) {
                    runnable.run(AuthStore.getInstance().getUser());
                }
            }
        }

        public void onLoginDone(Runnable runnable) {
            this.runnable = runnable;
        }
    }
}
