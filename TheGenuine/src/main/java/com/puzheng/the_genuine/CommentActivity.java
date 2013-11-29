package com.puzheng.the_genuine;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.Misc;

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
                if(Misc.isEmptyString(comment)){
                    Toast.makeText(CommentActivity.this, R.string.add_comment_empty, Toast.LENGTH_SHORT).show();
                }else{
                    new NewCommentTask().execute(new Pair<String, Float>(comment, mRatingBar.getRating()));
                }
            }
        });
    }

    class NewCommentTask extends AsyncTask<Pair<String, Float>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Pair<String, Float>... params) {
            try {
                String comment = params[0].first;
                float rating = params[0].second;
                return WebService.getInstance(CommentActivity.this).addComment(mProductID, comment, rating);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(CommentActivity.this, R.string.add_comment_succeed, Toast.LENGTH_SHORT).show();
                CommentActivity.this.finish();
            }else{
                Toast.makeText(CommentActivity.this, R.string.add_comment_failure, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
