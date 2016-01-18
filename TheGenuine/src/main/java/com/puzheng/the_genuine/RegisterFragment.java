package com.puzheng.the_genuine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.the_genuine.model.User;
import com.puzheng.the_genuine.store.AuthStore;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-17.
 */
public class RegisterFragment extends Fragment {
    private EditText emailView;
    private EditText passwordView;
    private EditText passwordConfirmView;
    private View mRegisterForm;
    private View mRegisterStatusView;
    private TextView mRegisterStatusMessageView;
    private String email;
    private String password;
    private String mPasswordConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        emailView = (EditText) rootView.findViewById(R.id.email);

        passwordView = (EditText) rootView.findViewById(R.id.password);
        passwordConfirmView = (EditText) rootView.findViewById(R.id.textPasswordConfirm);
        passwordConfirmView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_ACTION_GO) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
        mRegisterForm = rootView.findViewById(R.id.register_form);
        mRegisterStatusView = rootView.findViewById(R.id.register_status);
        mRegisterStatusMessageView = (TextView) rootView.findViewById(R.id.register_status_message);

        rootView.findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        ((ToggleButton) rootView.findViewById(R.id.togglePasswordVisibility)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                passwordView.setInputType(isChecked ?
                                InputType.TYPE_CLASS_TEXT :
                                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                passwordView.setSelection(passwordView.getText().length());
            }
        });

        return rootView;
    }

    private void attemptRegister() {
        hideKeyboard();


        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);
        passwordConfirmView.setError(null);

        email = emailView.getText().toString();
        password = passwordView.getText().toString();
        mPasswordConfirm = passwordConfirmView.getText().toString();

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

        // Check for a valid password.
        if (TextUtils.isEmpty(mPasswordConfirm)) {
            passwordConfirmView.setError(getString(R.string.error_field_required));
            focusView = passwordConfirmView;
            failed = true;
        } else if (mPasswordConfirm.length() < 4) {
            passwordConfirmView.setError(getString(R.string.error_invalid_password));
            focusView = passwordConfirmView;
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
        } else if (email.length() >= Integer.parseInt(getString(R.string.email_max_length))) {
            emailView.setError(getString(R.string.email_max_length_error));
            focusView = emailView;
            failed = true;
        }

        if (!mPasswordConfirm.equals(password)) {
            passwordView.setError(getString(R.string.error_incorrect_confirm_password));
            passwordView.setText("");
            passwordConfirmView.setText("");
            focusView = passwordView;
            failed = true;
        }

        if (failed) {
            focusView.requestFocus();
        } else {
            mRegisterStatusMessageView.setText(R.string.register_progressing);
            showProgress(true);
            AuthStore.getInstance().register(email, password).done(new DoneHandler<User>() {
                @Override
                public void done(User user) {
                    Logger.i("user registered");
                    Logger.json(new Gson().toJson(user));
                    Activity activity = getActivity();
                    Toast.makeText(activity, "注册成功", Toast.LENGTH_SHORT).show();
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                }
            }).fail(new FailHandler<Pair<String, String>>() {
                @Override
                public void fail(Pair<String, String> arg) {
                    Logger.i("register failed");
                    String text = arg.first == AuthStore.EMAIL_EXISTS? arg.second: "注册出错了!";
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                    // why? since showProgress will prevent focusing
                    emailView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            emailView.requestFocus();
                        }
                    }, 300);
                }
            }).always(new AlwaysHandler() {
                @Override
                public void always() {
                    showProgress(false);
                }
            });
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterStatusView.setVisibility(View.VISIBLE);
            mRegisterStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mRegisterForm.setVisibility(View.VISIBLE);
            mRegisterForm.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
