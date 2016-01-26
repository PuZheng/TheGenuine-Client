package com.puzheng.lejian;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.*;
import android.widget.Toast;

import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.adapter.CommentListAdapter;
import com.puzheng.lejian.model.Comment;
import com.puzheng.lejian.store.AuthStore;
import com.puzheng.lejian.store.CommentStore;

import java.util.List;

public class CommentListActivity extends ListActivity implements RefreshInterface {
    private MaskableManager maskableManager;
    private int spuId;
    private View emptyView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newComment:
                if (AuthStore.getInstance().getUser() == null) {
                    MyApp.doLoginIn(CommentListActivity.this);
                } else {
                    addComment();
                }
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
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApp.LOGIN_ACTION) {
                addComment();
            }
        }
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

    private void addComment() {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(Const.TAG_SPU_ID, spuId);
        startActivity(intent);
    }

}
