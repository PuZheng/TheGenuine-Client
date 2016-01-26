package com.puzheng.lejian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.puzheng.lejian.CommentListActivity;
import com.puzheng.lejian.Const;
import com.puzheng.lejian.R;
import com.puzheng.lejian.model.Comment;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentListAdapter extends BaseAdapter {

    private CommentListActivity commentListActivity;
    private final List<Comment> comments;
    private final LayoutInflater inflater;

    public CommentListAdapter(CommentListActivity commentListActivity, List<Comment> comments) {
        this.commentListActivity = commentListActivity;
        this.comments = comments;
        inflater = LayoutInflater.from(commentListActivity.getApplication());
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
            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                    (TextView) convertView.findViewById(R.id.textViewEmail),
                    (RatingBar) convertView.findViewById(R.id.ratingBar),
                    (TextView) convertView.findViewById(R.id.textViewContent),
                    (TextView) convertView.findViewById(R.id.textViewCreatedAt));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Comment comment = (Comment) getItem(position);
        Glide.with(commentListActivity.getApplication()).load(comment.getUser().getAvatar()).into(viewHolder.avatar);
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
