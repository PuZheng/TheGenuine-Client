package com.puzheng.lejian;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.model.Comment;
import com.puzheng.lejian.model.User;
import com.puzheng.lejian.store.CommentStore;
import com.puzheng.lejian.util.LoginRequired;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentListActivity extends AppCompatActivity implements RefreshInterface, LoginRequired.ILoginHandler {
    private static final int COMMENT_ACTION = 1;
    private static final int LOGIN_ACTION = 2;
    private int spuId;
    private LoginRequired.LoginHandler loginHandler;
    private List<Comment> comments;
    private ListFragment commentListFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newComment:
                LoginRequired loginRequired = LoginRequired.with(this).requestCode(LOGIN_ACTION);
                loginHandler = loginRequired.createLoginHandler();
                loginRequired.wraps(new LoginRequired.Runnable() {
                    @Override
                    public void run(User user) {
                        Intent intent = new Intent(CommentListActivity.this, CommentActivity.class);
                        intent.putExtra(Const.TAG_SPU_ID, spuId);
                        startActivityForResult(intent, COMMENT_ACTION);
                    }
                });
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void refresh() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loginHandler.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == COMMENT_ACTION) {
            comments.add(0, (Comment) data.getParcelableExtra(CommentActivity.TAG_COMMENT));
            ((BaseAdapter) commentListFragment.getListAdapter()).notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        spuId = getIntent().getIntExtra(Const.TAG_SPU_ID, 0);

        final ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        commentListFragment = (ListFragment) getFragmentManager().findFragmentById(R.id.comments);
        CommentStore.getInstance().fetchList(spuId).done(new DoneHandler<List<Comment>>() {
            @Override
            public void done(List<Comment> comments) {
                CommentListActivity.this.comments = comments;
                commentListFragment.setListAdapter(new CommentListAdapter(comments));
                if (comments.size() == 0) {
                    commentListFragment.setEmptyText(getString(R.string.no_result_found));
                }
                String text = getString(R.string.comment_cnt) + "(" +
                        Humanize.with(CommentListActivity.this).num(comments.size()) + ")";
                supportActionBar.setTitle(text);
            }
        }).fail(new FailHandler<Pair<String, String>>() {
            @Override
            public void fail(Pair<String, String> stringStringPair) {
                Toast.makeText(CommentListActivity.this,
                        getString(R.string.load_comment_list_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoginDone(LoginRequired.Runnable runnable) {
        loginHandler.onLoginDone(runnable);
    }


    public static class CommentListAdapter extends BaseAdapter {

        private final List<Comment> comments;
        private final LayoutInflater inflater;

        public CommentListAdapter(List<Comment> comments) {
            this.comments = comments;
            inflater = LayoutInflater.from(MyApp.getContext());
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.comment_list_item, null);
            }
            ViewHolder viewHolder;
            if (convertView.getTag() == null) {
                viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageViewVerified),
                        (TextView) convertView.findViewById(R.id.textViewEmail),
                        (RatingBar) convertView.findViewById(R.id.ratingBar),
                        (TextView) convertView.findViewById(R.id.textViewContent),
                        (TextView) convertView.findViewById(R.id.textViewCreatedAt));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Comment comment = (Comment) getItem(position);
            Glide.with(MyApp.getContext()).load(comment.getUser().getAvatar()).into(viewHolder.avatar);
            viewHolder.textViewEmail.setText(comment.getUser().getEmail());
            viewHolder.ratingBar.setRating(comment.getRating());
            viewHolder.textViewContent.setText(comment.getContent());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Const.DATE_FORMAT);
            viewHolder.textViewCreatedAt.setText(simpleDateFormat.format(comment.getCreatedAt()));
            return convertView;
        }

        private class ViewHolder {
            ImageView avatar;
            TextView textViewEmail;
            RatingBar ratingBar;
            TextView textViewContent;
            TextView textViewCreatedAt;

            ViewHolder(ImageView avatar, TextView textViewEmail, RatingBar ratingBar,
                       TextView textViewContent, TextView textViewCreatedAt) {
                this.avatar = avatar;
                this.textViewEmail = textViewEmail;
                this.ratingBar = ratingBar;
                this.textViewContent = textViewContent;
                this.textViewCreatedAt = textViewCreatedAt;
            }
        }
    }
}
