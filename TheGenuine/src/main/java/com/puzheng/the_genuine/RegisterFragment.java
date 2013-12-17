package com.puzheng.the_genuine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.BadResponseException;

import java.io.IOException;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-17.
 */
public class RegisterFragment extends Fragment {
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private View mRegisterForm;
    private View mRegisterStatusView;
    private TextView mRegisterStatusMessageView;
    private UserRegisterTask mRegisterTask = null;
    private String mEmail;
    private String mPassword;
    private String mPasswordConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        mEmailView = (EditText) rootView.findViewById(R.id.email);

        mPasswordView = (EditText) rootView.findViewById(R.id.password);
        mPasswordConfirmView = (EditText) rootView.findViewById(R.id.textPasswordConfirm);
        mPasswordConfirmView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        initSwitch((Switch) rootView.findViewById(R.id.switch1));

        return rootView;
    }

    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        mPasswordConfirm = mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(mPasswordConfirm)) {
            mPasswordConfirmView.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirmView;
            cancel = true;
        } else if (mPasswordConfirm.length() < 4) {
            mPasswordConfirmView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordConfirmView;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (!mPasswordConfirm.equals(mPassword)) {
            mPasswordView.setError(getString(R.string.error_incorrect_confirm_password));
            mPasswordView.setText("");
            mPasswordConfirmView.setText("");
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mRegisterStatusMessageView.setText(R.string.register_progressing);
            showProgress(true);
            mRegisterTask = new UserRegisterTask();
            mRegisterTask.execute();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initSwitch(Switch switch_) {
        switch_.setChecked(true);
        switch_.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int inputType = isChecked ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT;
                mPasswordView.setInputType(inputType);
            }
        });
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

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
        private BadResponseException exception;
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                User user = WebService.getInstance(RegisterFragment.this.getActivity()).register(mEmail, mPassword);
                if (user != null) {
                    MyApp.setCurrentUser(user);
                    return true;
                }
            } catch (BadResponseException e) {
                exception = e;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            showProgress(false);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
            showProgress(false);
            if (success) {
                Activity activity = getActivity();
                Toast.makeText(activity, "注册成功", Toast.LENGTH_SHORT).show();
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            } else {
                if (exception != null) {
                    mEmailView.setError(exception.getMessage());
                }else{
                    mEmailView.setError(getString(R.string.action_register_fail));
                }
                mPasswordView.setText("");
                mPasswordConfirmView.setText("");
                mEmailView.requestFocus();
            }
        }
    }
}
