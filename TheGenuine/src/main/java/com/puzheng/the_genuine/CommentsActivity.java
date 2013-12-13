package com.puzheng.the_genuine;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.puzheng.the_genuine.data_structure.Comment;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.utils.Misc;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentsActivity extends ListActivity implements Maskable {

    private int spuId;
    private TextView mCountTextView;

    private View mask;
    private View main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        // Show the Up button in the action bar.
        spuId = getIntent().getIntExtra(Constants.TAG_SPU_ID, 0);

        mask = findViewById(R.id.mask);
        main = findViewById(R.id.main);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getActionBar().setCustomView(R.layout.comments_title);
            View view = getActionBar().getCustomView();
            ImageButton backButton = (ImageButton) view.findViewById(R.id.imageButtonBack);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            ImageButton newCommentButton = (ImageButton) view.findViewById(R.id.imageButtonNew);
            newCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyApp.getCurrentUser() == null) {
                        MyApp.doLoginIn(CommentsActivity.this);
                    } else {
                        addComment();
                    }
                }
            });
            mCountTextView = (TextView) view.findViewById(R.id.textView);
        }
    }

    private void addComment() {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(Constants.TAG_SPU_ID, spuId);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApp.LOGIN_ACTION) {
                addComment();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comments, menu);
        return true;
    }

    @Override
    public void mask() {
        main.setVisibility(View.GONE);
        mask.setVisibility(View.VISIBLE);
    }

    @Override
    public void unmask(Boolean b) {
        main.setVisibility(View.VISIBLE);
        mask.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetCommentsTask(this).execute(spuId);
    }

    private class GetCommentsTask extends AsyncTask<Integer, Void, List<Comment>> {
        private final ListActivity listActivity;
        private final Maskable maskable;

        public GetCommentsTask(ListActivity listActivity) {
            this.listActivity = listActivity;
            this.maskable = (Maskable) listActivity;
        }

        @Override
        protected List<Comment> doInBackground(Integer... params) {
            int productId = params[0];
            try {
                return WebService.getInstance(this.listActivity).getComments(productId);
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPreExecute() {
            maskable.mask();
        }

        @Override
        protected void onPostExecute(List<Comment> commentList) {
            boolean b = commentList != null;
            String text = "加载评论失败";
            if (b) {
                listActivity.setListAdapter(new MyCommentsAdapter(commentList));
                text = "评论(" + Misc.humanizeNum(commentList.size()) + ")";
            }
            if (mCountTextView != null) {
                mCountTextView.setText(text);
            }
            this.maskable.unmask(b);
        }
    }

    private class MyCommentsAdapter extends BaseAdapter {

        private final List<Comment> comments;
        private final LayoutInflater inflater;

        public MyCommentsAdapter(List<Comment> comments) {
            this.comments = comments;
            inflater = (LayoutInflater) CommentsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Object getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return comments.get(position).getId();
        }

        private class ViewHolder {
            ImageView avatar;
            TextView textViewUserName;
            RatingBar ratingBar;
            TextView textViewContent;
            TextView textViewDate;

            ViewHolder(ImageView avatar, TextView textViewUserName, RatingBar ratingBar,
                       TextView textViewContent, TextView textViewDate) {
                this.avatar = avatar;
                this.textViewUserName = textViewUserName;
                this.ratingBar = ratingBar;
                this.textViewContent = textViewContent;
                this.textViewDate = textViewDate;
            }
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.comment_list_item, null);
            }
            ViewHolder viewHolder;
            if (convertView.getTag() == null) {
                viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                        (TextView) convertView.findViewById(R.id.textViewUserName),
                        (RatingBar) convertView.findViewById(R.id.ratingBar),
                        (TextView) convertView.findViewById(R.id.textViewContent),
                        (TextView) convertView.findViewById(R.id.textViewDate));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Comment comment = (Comment) getItem(position);
            new GetImageTask(viewHolder.avatar, comment.getUserSmallAvatar()).execute();
            viewHolder.textViewUserName.setText(comment.getUserName());
            viewHolder.ratingBar.setRating(comment.getRating());
            viewHolder.textViewContent.setText(comment.getContent());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
            viewHolder.textViewDate.setText(simpleDateFormat.format(comment.getDate()));
            if (Misc.isEmptyString(comment.getUserSmallAvatar())) {
                new GetImageTask(viewHolder.avatar, comment.getUserSmallAvatar()).execute();
            }
            return convertView;
        }
    }
}
