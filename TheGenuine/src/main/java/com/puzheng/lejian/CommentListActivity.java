package com.puzheng.lejian;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.adapter.CommentListAdapter;
import com.puzheng.lejian.model.Comment;
import com.puzheng.lejian.model.User;
import com.puzheng.lejian.store.CommentStore;
import com.puzheng.lejian.util.LoginRequired;

import java.util.List;

public class CommentListActivity extends ListActivity implements RefreshInterface, LoginRequired.ILoginHandler {
    private static final int COMMENT_ACTION = 1;
    private static final int LOGIN_ACTION = 2;
    private MaskableManager maskableManager;
    private int spuId;
    private View emptyView;
    private LoginRequired.LoginHandler loginHandler;
    private List<Comment> comments;

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
            ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        maskableManager = new MaskableManager(getListView(), this);
        // Show the Up buttonNearby in the action bar.
        spuId = getIntent().getIntExtra(Const.TAG_SPU_ID, 0);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        emptyView = getListView().getEmptyView();
        emptyView.setVisibility(View.GONE);
        maskableManager.mask();
        CommentStore.getInstance().fetchList(spuId).done(new DoneHandler<List<Comment>>() {
            @Override
            public void done(List<Comment> comments) {
                CommentListActivity.this.comments = comments;
                setListAdapter(new CommentListAdapter(CommentListActivity.this, comments));
                if (comments.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                }
                String text = getString(R.string.comment_cnt) + "(" +
                        Humanize.with(CommentListActivity.this).num(comments.size()) + ")";
                getActionBar().setTitle(text);
            }
        }).fail(new FailHandler<Pair<String, String>>() {
            @Override
            public void fail(Pair<String, String> stringStringPair) {
                Toast.makeText(CommentListActivity.this,
                        getString(R.string.load_comment_list_failed), Toast.LENGTH_SHORT).show();
            }
        }).always(new AlwaysHandler() {
            @Override
            public void always() {
                maskableManager.unmask(null);
            }
        });
    }

    @Override
    public void onLoginDone(LoginRequired.Runnable runnable) {
        loginHandler.onLoginDone(runnable);
    }
}
