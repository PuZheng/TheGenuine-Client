package com.puzheng.lejian;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import com.puzheng.lejian.netutils.WebService;
import com.puzheng.lejian.util.PoliteBackgroundTask;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-29.
 */
public class CommentActivity extends Activity {
    private int mSpuId;
    private EditText mEditText;
    private RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mSpuId = getIntent().getIntExtra(Constants.TAG_SPU_ID, 0);
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
                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(CommentActivity.this, R.string.add_comment_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                PoliteBackgroundTask.Builder<Void> builder = new PoliteBackgroundTask.Builder<Void>(CommentActivity.this);
                builder.msg(getString(R.string.committing));
                final String finalComment = comment;
                builder.run(new PoliteBackgroundTask.XRunnable<Void>() {
                    @Override
                    public Void run() throws Exception {
                        WebService.getInstance(CommentActivity.this).addComment(mSpuId, finalComment, mRatingBar.getRating());
                        return null;
                    }
                });
                builder.after(new PoliteBackgroundTask.OnAfter<Void>() {
                    @Override
                    public void onAfter(Void v) {
                        Toast.makeText(CommentActivity.this, R.string.add_comment_succeed, Toast.LENGTH_SHORT).show();
                        CommentActivity.this.finish();
                    }
                });
                builder.create().start();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
