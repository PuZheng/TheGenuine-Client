package com.puzheng.lejian.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.Const;
import com.puzheng.lejian.MyApp;
import com.puzheng.lejian.NearbyActivity;
import com.puzheng.lejian.R;
import com.puzheng.lejian.model.SPU;

import java.util.List;


public class RecommendationListAdapter extends BaseAdapter {
    private final List<SPU> spus;
    private final LayoutInflater inflater;

    public RecommendationListAdapter(List<SPU> spus) {
        this.spus = spus;
        inflater = LayoutInflater.from(MyApp.getContext());
    }

    @Override
    public int getCount() {
        return spus.size();
    }

    @Override
    public Object getItem(int position) {
        return spus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return spus.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spu_list_item, null);
        }
        ViewHolder viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                    (TextView) convertView.findViewById(R.id.textViewName),
                    (TextView) convertView.findViewById(R.id.textViewFavorCnt),
                    (TextView) convertView.findViewById(R.id.textViewMSRP),
                    (Button) convertView.findViewById(R.id.btnNearby),
                    (RatingBar) convertView.findViewById(R.id.ratingBar));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Context context = MyApp.getContext();
        final SPU spu = (SPU) getItem(position);
        Glide.with(MyApp.getContext()).load(spu.getIcon().getURL()).into(viewHolder.imageView);

        viewHolder.textViewName.setText(spu.getName());
        viewHolder.textViewFavorCnt.setText(context.getString(R.string.popularity,
                Humanize.with(context).num(spu.getFavorCnt())));
        viewHolder.textViewMSRP.setText(String.valueOf(spu.getMSRP()));
        viewHolder.ratingBar.setRating(spu.getRating());
        viewHolder.buttonNearby.setText(context.getString(R.string.nearest,
                Humanize.with(context).distance(spu.getDistance())));
        viewHolder.buttonNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NearbyActivity.class);
                intent.putExtra("current", NearbyActivity.NEARBY_LIST);
                intent.putExtra(Const.TAG_SPU_ID, spu.getId());
                context.startActivity(intent);
            }
        });
        if (spu.getDistance() == -1) {
            viewHolder.buttonNearby.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public static class ViewHolder {
        public ImageView imageView;
        public TextView textViewName;
        public TextView textViewMSRP;
        TextView textViewFavorCnt;
        public Button buttonNearby;
        public RatingBar ratingBar;

        public ViewHolder(ImageView imageView, TextView textViewName,
                          TextView textViewFavorCnt, TextView textViewMSRP, Button buttonNearby, RatingBar ratingBar) {
            this.imageView = imageView;
            this.textViewName = textViewName;
            this.textViewFavorCnt = textViewFavorCnt;
            this.textViewMSRP = textViewMSRP;
            this.buttonNearby = buttonNearby;
            this.ratingBar = ratingBar;
        }
    }
}