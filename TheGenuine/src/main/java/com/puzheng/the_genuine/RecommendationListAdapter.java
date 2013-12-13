package com.puzheng.the_genuine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.utils.Misc;

import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-13.
 */

public class RecommendationListAdapter extends BaseAdapter {
    private final List<Recommendation> recommendations;
    private final LayoutInflater inflater;
    private final Activity mActivity;

    public RecommendationListAdapter(List<Recommendation> recommendations, Activity activity) {
        this.recommendations = recommendations;
        this.mActivity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return recommendations.size();
    }

    @Override
    public Object getItem(int position) {
        return recommendations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recommendations.get(position).getSPUId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recommendation_list_item, null);
        }
        ViewHolder viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                    (TextView) convertView.findViewById(R.id.textViewProductName),
                    (TextView) convertView.findViewById(R.id.textViewFavorCnt),
                    (TextView) convertView.findViewById(R.id.textViewPrice),
                    (Button) convertView.findViewById(R.id.button),
                    (RatingBar) convertView.findViewById(R.id.ratingBar));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Recommendation recommendation = (Recommendation) getItem(position);
        new GetImageTask(viewHolder.imageView, recommendation.getPicUrl()).execute();

        viewHolder.textViewProductName.setText(recommendation.getProductName());
        viewHolder.textViewFavorCnt.setText("人气" + Misc.humanizeNum(recommendation.getFavorCnt()));
        viewHolder.textViewPrice.setText(String.valueOf(recommendation.getPriceInYuan()));
        viewHolder.ratingBar.setRating(recommendation.getRating());
        viewHolder.button.setText("最近" + Misc.humanizeDistance(recommendation.getDistance()));
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, NearbyActivity.class);
                intent.putExtra("current", NearbyActivity.NEARBY_LIST);
                intent.putExtra(Constants.TAG_SPU_ID, recommendation.getSPUId());
                mActivity.startActivity(intent);
            }
        });
        if (recommendation.getDistance() == -1) {
            viewHolder.button.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public static class ViewHolder {
        ImageView imageView;
        TextView textViewProductName;
        TextView textViewPrice;
        TextView textViewFavorCnt;
        Button button;
        RatingBar ratingBar;

        ViewHolder(ImageView imageView, TextView textViewProductName,
                   TextView textViewFavorCnt, TextView textViewPrice, Button button, RatingBar ratingBar) {
            this.imageView = imageView;
            this.textViewProductName = textViewProductName;
            this.textViewFavorCnt = textViewFavorCnt;
            this.textViewPrice = textViewPrice;
            this.button = button;
            this.ratingBar = ratingBar;
        }
    }
}