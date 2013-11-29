package com.puzheng.the_genuine;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import com.puzheng.the_genuine.data_structure.Comment;
import com.puzheng.the_genuine.data_structure.VerificationInfo;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.Misc;
import com.puzheng.the_genuine.utils.PoliteBackgroundTask;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-29.
 */
public class CommentActivity extends Activity {
    private int mProductID;
    private EditText mEditText;
    private RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mProductID = getIntent().getIntExtra(ProductActivity.TAG_PRODUCT_ID, 0);
        mEditText = (EditText) findViewById(R.id.editText);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignore) {
        }
        Button confirmButton = (Button) findViewById(R.id.ok);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = "";
                try {
                    comment = mEditText.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Misc.isEmptyString(comment)) {
                    Toast.makeText(CommentActivity.this, R.string.add_comment_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                PoliteBackgroundTask.Builder<Boolean> builder = new PoliteBackgroundTask.Builder<Boolean>(CommentActivity.this);
                builder.msg("正在提交");
                final String finalComment = comment;
                builder.run(new PoliteBackgroundTask.XRunnable<Boolean>() {
                    @Override
                    public Boolean run() throws Exception {
                        return WebService.getInstance(CommentActivity.this).addComment(mProductID, finalComment, mRatingBar.getRating());
                    }
                });
                builder.after(new PoliteBackgroundTask.OnAfter<Boolean>() {
                    @Override
                    public void onAfter(Boolean aBoolean) {
                        if (aBoolean) {
                            Toast.makeText(CommentActivity.this, R.string.add_comment_succeed, Toast.LENGTH_SHORT).show();
                            CommentActivity.this.finish();
                        } else {
                            Toast.makeText(CommentActivity.this, R.string.add_comment_failure, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.create().start();
            }
        });
    }
}
