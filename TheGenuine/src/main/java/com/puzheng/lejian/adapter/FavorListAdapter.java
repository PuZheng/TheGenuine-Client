package com.puzheng.lejian.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.Const;
import com.puzheng.lejian.FavorListActivity;
import com.puzheng.lejian.MyApp;
import com.puzheng.lejian.NearbyActivity;
import com.puzheng.lejian.R;
import com.puzheng.lejian.model.Favor;

import java.util.List;

/**
 * Created by xc on 16-1-27.
 */
public class FavorListAdapter extends BaseAdapter {
    private List<Favor> favors;
    private LayoutInflater inflater;

    public FavorListAdapter(List<Favor> favors) {
        inflater = LayoutInflater.from(MyApp.getContext());
        this.favors = favors;
    }

    @Override
    public int getCount() {
        return favors.size();
    }

    @Override
    public Object getItem(int position) {
        return favors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return favors.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spu_list_item, null);
        }
        RecommendationListAdapter.ViewHolder viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = new RecommendationListAdapter.ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                    (TextView) convertView.findViewById(R.id.textViewName),
                    (TextView) convertView.findViewById(R.id.textViewFavorCnt),
                    (TextView) convertView.findViewById(R.id.textViewMSRP),
                    (Button) convertView.findViewById(R.id.btnNearby),
                    (RatingBar) convertView.findViewById(R.id.ratingBar));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RecommendationListAdapter.ViewHolder) convertView.getTag();
        }

        final Favor favor = (Favor) getItem(position);
        Glide.with(MyApp.getContext()).load(favor.getSPU().getIcon().getURL()).into(viewHolder.imageView);

        viewHolder.textViewName.setText(favor.getSPU().getName());
        viewHolder.textViewMSRP.setText("￥" + favor.getSPU().getMSRP());
        viewHolder.textViewFavorCnt.setText(favorListActivity.getString(R.string.popularity,
                Humanize.with(MyApp.getContext()).num(favor.getFavorCnt())));
        viewHolder.ratingBar.setRating(favor.getSPU().getRating());
        viewHolder.buttonNearby.setText(favorListActivity.getString(R.string.nearest,
                Humanize.with(MyApp.getContext()).distance(favor.getDistance())));
        viewHolder.buttonNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApp.getCurrentActivity(), NearbyActivity.class);
                intent.putExtra("current", NearbyActivity.NEARBY_LIST);
                intent.putExtra(Const.TAG_SPU_ID, favor.getSPU().getId());
                MyApp.getCurrentActivity().startActivity(intent);
            }
        });
        if (favor.getDistance() == -1) {
            viewHolder.buttonNearby.setVisibility(View.INVISIBLE);
        }

        viewHolder.buttonNearby.setText(Humanize.with(MyApp.getContext()).distance(favor.getDistance()));
        viewHolder.textViewMSRP.setText(String.valueOf(favor.getSPU().getMSRP()));
        return convertView;
    }

}
