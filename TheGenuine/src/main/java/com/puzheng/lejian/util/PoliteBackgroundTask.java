package com.puzheng.lejian.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;

import java.util.Timer;
import java.util.TimerTask;

public class PoliteBackgroundTask<Result> {

    private final static String ERROR = "错误";
    private final static String UNKNOWN_ERROR = "未知错误";
    private final static String I_SEE = "我知道了";
    private XRunnable<Result> mRunnable = null;
    private CharSequence mMsg = null;
    private OnAfter<Result> mAfter = null;
    private ExceptionHandler mExceptionHandler = null;
    private Context mContext = null;
    private Exception mException;
    private MessageGenerator<Result> messageGenerator;
    private Result xRunnableResult;

    PoliteBackgroundTask(XRunnable<Result> runnable, CharSequence msg, MessageGenerator<Result> messageGenerator, OnAfter<Result> after,
                         ExceptionHandler exceptionHandler, Context context) {
        mRunnable = runnable;
        mMsg = msg;
        this.messageGenerator = messageGenerator;
        mContext = context;
        mAfter = after;
        mExceptionHandler = exceptionHandler;
    }

    public void start() {

        final ProgressDialog pd = new ProgressDialog(mContext);
        pd.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            xRunnableResult = mRunnable.run();
                        } catch (Exception e) {
                            mException = e;
                        }
                        pd.cancel();
                    }
                }.start();
            }
        });
        pd.setCancelable(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mException == null) { // success
                    final AlertDialog ad = new AlertDialog.Builder(mContext).create();
                    ad.setMessage(PoliteBackgroundTask.this.messageGenerator.genMessage(xRunnableResult));
                    ad.setOnShowListener(new DialogInterface.OnShowListener() {

                        public void onShow(DialogInterface dialog) {
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {

                                @Override
                                public void run() {
                                    ad.cancel();
                                }

                            }, 1000);
                        }
                    });
                    ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            if (mAfter != null) {
                                mAfter.onAfter(xRunnableResult);
                            }
                        }
                    });
                    ad.show();
                } else {
                    String errMsg = mException.getMessage();
                    if (TextUtils.isEmpty(errMsg)) {
                        errMsg = UNKNOWN_ERROR;
                    }
                    if (mExceptionHandler != null) {
                        mExceptionHandler.run(mException);
                    } else {
                        final AlertDialog ad = new AlertDialog.Builder(mContext).create();
                        ad.setMessage(errMsg);
                        ad.setTitle(ERROR);
                        ad.setButton(DialogInterface.BUTTON_NEGATIVE, I_SEE, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ad.dismiss();
                            }
                        });
                        ad.show();
                    }
                }
            }
        });
        pd.setMessage(mMsg);
        pd.show();
    }

    public interface XRunnable<Result> {
        public Result run() throws Exception;
    }

    public interface OnAfter<Result> {
        public void onAfter(Result result);
    }
    public interface ExceptionHandler {
        public void run(Exception e);
    }

    public interface MessageGenerator<Result> {
        public String genMessage(Result result);
    }

    public static class Builder<Result> {
        private CharSequence _mMsg = null;
        private String _mOkMsg;
        private OnAfter<Result> _mAfter;
        private Context _mContext;
        private XRunnable<Result> _mRunnable;
        private ExceptionHandler _mExceptionHandler;
        private MessageGenerator<Result> messageGenerator;

        public Builder(Context context) {
            _mContext = context;
            messageGenerator = new MessageGenerator<Result>() {

                @Override
                public String genMessage(Result result) {
                    return _mOkMsg;
                }
            };
        }

        public Builder<Result> after(OnAfter after) {
            _mAfter = after;
            return this;
        }

        public PoliteBackgroundTask<Result> create() {
            return new PoliteBackgroundTask<Result>(_mRunnable, _mMsg, this.messageGenerator, _mAfter,
                    _mExceptionHandler, _mContext);
        }

        public Builder<Result> exceptionHandler(ExceptionHandler exceptionHandler) {
            _mExceptionHandler = exceptionHandler;
            return this;
        }

        public Builder<Result> msg(String msg) {
            _mMsg = msg;
            return this;
        }

        public Builder<Result> okMsg(String okMsg) {
            _mOkMsg = okMsg;
            return this;
        }

        public Builder<Result> run(XRunnable<Result> runnable) {
            _mRunnable = runnable;
            return this;
        }

        public Builder<Result> setOkMessageGenerator(MessageGenerator<Result> messageGenerator) {
            this.messageGenerator = messageGenerator;
            return this;
        }
    }
}
