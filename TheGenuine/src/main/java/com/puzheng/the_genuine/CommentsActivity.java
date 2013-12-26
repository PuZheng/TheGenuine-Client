package com.puzheng.the_genuine;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.puzheng.the_genuine.data_structure.Comment;
import com.puzheng.the_genuine.image_utils.ImageFetcher;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.Misc;
import com.puzheng.the_genuine.views.CustomActionBar;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentsActivity extends ListActivity implements RefreshInterface {
    private MaskableManager maskableManager;
    private int spuId;
    private CustomActionBar mCustomActionBar;
    private GetCommentsTask task;
    private ImageFetcher mImageFetcher;

    @Override
    public void refresh() {
        if (task == null) {
            task = new GetCommentsTask(this);
            task.execute(spuId);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mCustomActionBar = CustomActionBar.setCustomerActionBar(getActionBar(), CommentsActivity.this);
        mCustomActionBar.setUpButtonEnable(true);
        maskableManager = new MaskableManager(getListView(), this);
        // Show the Up button in the action bar.
        spuId = getIntent().getIntExtra(Constants.TAG_SPU_ID, 0);
        int imageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_view_list_item_width);
        mImageFetcher = ImageFetcher.getImageFetcher(this, imageThumbSize, 0.25f); // Set memory cache to 25% of app memory
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.imageButtonNew:
                    if (MyApp.getCurrentUser() == null) {
                        MyApp.doLoginIn(CommentsActivity.this);
                    } else {
                        addComment();
                    }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (task == null) {
            task = new GetCommentsTask(this);
            task.execute(spuId);
        }
    }

    private class GetCommentsTask extends AsyncTask<Integer, Void, List<Comment>> {
        private final ListActivity listActivity;
        private Exception exception;

        public GetCommentsTask(ListActivity listActivity) {
            this.listActivity = listActivity;
        }

        @Override
        protected List<Comment> doInBackground(Integer... params) {
            try {
                int productId = params[0];
                return WebService.getInstance(this.listActivity).getComments(productId);
            } catch (Exception e) {
                exception = e;
                return null;
            }

        }

        @Override
        protected void onPreExecute() {
            maskableManager.mask();
        }

        @Override
        protected void onPostExecute(List<Comment> commentList) {
            String text = "加载评论失败";
            if (maskableManager.unmask(exception)) {
                listActivity.setListAdapter(new MyCommentsAdapter(commentList));
                text = "评论(" + Misc.humanizeNum(commentList.size()) + ")";
            }
            mCustomActionBar.setTitle(text);
            task = null;
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
            mImageFetcher.loadImage(comment.getUserSmallAvatar(), viewHolder.avatar);
            viewHolder.textViewUserName.setText(comment.getUserName());
            viewHolder.ratingBar.setRating(comment.getRating());
            viewHolder.textViewContent.setText(comment.getContent());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
            viewHolder.textViewDate.setText(simpleDateFormat.format(comment.getDate()));
            return convertView;
        }
    }
}
