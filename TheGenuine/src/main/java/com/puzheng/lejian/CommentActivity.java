package com.puzheng.lejian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.lejian.model.Comment;
import com.puzheng.lejian.netutils.WebService;
import com.puzheng.lejian.store.AuthStore;
import com.puzheng.lejian.store.CommentStore;
import com.puzheng.lejian.util.PoliteBackgroundTask;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-29.
 */
public class CommentActivity extends Activity {
    public static final String TAG_COMMENT = "COMMENT";
    private int spuId;
    private EditText editText;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        spuId = getIntent().getIntExtra(Const.TAG_SPU_ID, 0);
        editText = (EditText) findViewById(R.id.editText);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignore) {
        }
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(CommentActivity.this, R.string.add_comment_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                Comment comment = new Comment.Builder().rating(ratingBar.getRating())
                        .content(content).userId(AuthStore.getInstance().getUser().getId())
                        .spuId(spuId)
                        .build();
                CommentStore.getInstance().create(comment).done(new DoneHandler<Comment>() {
                    @Override
                    public void done(Comment comment) {
                        Toast.makeText(CommentActivity.this, R.string.add_comment_succeed, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra(TAG_COMMENT, comment);
                        CommentActivity.this.setResult(RESULT_OK, intent);
                        CommentActivity.this.finish();
                    }
                }).fail(new FailHandler<Pair<String, String>>() {
                    @Override
                    public void fail(Pair<String, String> err) {
                        Toast.makeText(getApplication(), R.string.add_comment_failure, Toast.LENGTH_SHORT).show();
                    }
                }).always(new AlwaysHandler() {
                    @Override
                    public void always() {

                    }
                });
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
