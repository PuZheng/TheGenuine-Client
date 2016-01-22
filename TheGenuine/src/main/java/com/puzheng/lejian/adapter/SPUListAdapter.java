package com.puzheng.lejian.adapter;

import android.content.Context;
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
import com.puzheng.lejian.R;
import com.puzheng.lejian.model.SPU;

import java.util.List;

public class SPUListAdapter extends BaseAdapter {

    private final List<SPU> spus;
    private final Context context;

    public SPUListAdapter(Context context, List<SPU> spus) {
        this.spus = spus;
        this.context = context;
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
        return ((SPU)getItem(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spu_list_item, parent, false);
        }
        ViewHolder viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                    (TextView) convertView.findViewById(R.id.textViewProductName),
                    (TextView) convertView.findViewById(R.id.textViewFavorCnt),
                    (TextView) convertView.findViewById(R.id.textViewPrice),
                    (Button) convertView.findViewById(R.id.btnNearby),
                    (RatingBar) convertView.findViewById(R.id.ratingBar));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SPU spu = (SPU) getItem(position);
        Glide.with(context).load(spu.getIcon().getURL())
                .error(R.drawable.ic_broken_image_black_24dp).into(viewHolder.imageView);

        viewHolder.textViewProductName.setText(spu.getName());
        viewHolder.textViewFavorCnt.setText(context.getString(R.string.popularity, Humanize.with(context).num(1200)));
        viewHolder.textViewPrice.setText(String.valueOf(spu.getMsrp()) + "元");
        viewHolder.ratingBar.setRating(spu.getRating());
        if (spu.getDistance() != 0) {
            viewHolder.button.setText(context.getString(R.string.nearest, Humanize.with(context).distance(spu.getDistance())));
            viewHolder.button.setVisibility(View.VISIBLE);
        } else {
            viewHolder.button.setVisibility(View.INVISIBLE);
        }
//        viewHolder.button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mActivity, NearbyActivity.class);
//                intent.putExtra("current", NearbyActivity.NEARBY_LIST);
//                intent.putExtra(Constants.TAG_SPU_ID, recommendation.getSPUId());
//                mActivity.startActivity(intent);
//            }
//        });
//        if (recommendation.getDistance() == -1) {
//            viewHolder.button.setVisibility(View.INVISIBLE);
//        }
        return convertView;
    }



    private static class ViewHolder {
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
