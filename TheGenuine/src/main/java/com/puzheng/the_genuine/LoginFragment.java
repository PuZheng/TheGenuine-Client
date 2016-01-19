package com.puzheng.the_genuine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.the_genuine.model.User;
import com.puzheng.the_genuine.store.AuthStore;

public class LoginFragment extends Fragment {
    private EditText emailView;
    private EditText passwordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView loginStatusMessageView;
    private String email;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        emailView = (EditText) rootView.findViewById(R.id.email);

        passwordView = (EditText) rootView.findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = rootView.findViewById(R.id.register_form);
        mLoginStatusView = rootView.findViewById(R.id.login_status);
        loginStatusMessageView = (TextView) rootView.findViewById(R.id.login_status_message);

        rootView.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        ((ToggleButton) rootView.findViewById(R.id.togglePasswordVisibility)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        passwordView.setInputType(isChecked ?
                                        InputType.TYPE_CLASS_TEXT :
                                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                        );
                        passwordView.setSelection(passwordView.getText().length());
                    }
                });
        TextView textView = (TextView) rootView.findViewById(R.id.register_textView);
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                getActivity().startActivityForResult(intent, RegisterActivity.TAG_REGISTER);
            }
        });

        return rootView;
    }

    private void attemptLogin() {

        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the register_or_login attempt.
        email = emailView.getText().toString();
        password = passwordView.getText().toString();

        boolean failed = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            failed = true;
        } else if (password.length() < 4) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            failed = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            failed = true;
        } else if (!email.contains("@")) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            failed = true;
        }

        if (failed) {
            // There was an error; don't attempt register_or_login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register_or_login attempt.
            loginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            AuthStore.getInstance().login(email, password).done(new DoneHandler<User>() {
                @Override
                public void done(User user) {
                    Logger.i("logged in");
                    Logger.i(new Gson().toJson(user));
                    Activity activity = getActivity();
                    Toast.makeText(activity, activity.getString(R.string.logined), Toast.LENGTH_SHORT).show();
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                }
            }).fail(new FailHandler<Pair<String, String>>() {
                @Override
                public void fail(Pair<String, String> error) {
                    Logger.i("log in failed");
                    Logger.i(new Gson().toJson(error));
                    if (error != null && error.first == AuthStore.INVALID_PASSWORD_OR_EMAIL) {
                        Toast.makeText(getActivity(), "错误的用户名或者密码", Toast.LENGTH_SHORT).show();
                        // WHY? since showProgress will make requestFocus failed
                        emailView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                emailView.requestFocus();
                            }
                        }, 300);
                    } else {
                        // TODO show unknown error, retry
                    }
                }
            }).always(new AlwaysHandler() {
                @Override
                public void always() {
                    showProgress(false);
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

