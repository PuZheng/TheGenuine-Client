package com.puzheng.lejian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.MyApp;
import com.puzheng.lejian.R;
import com.puzheng.lejian.model.Retailer;

import java.util.List;

public class NearbyListAdapter extends BaseAdapter {

    private List<Retailer> retailers;
    private final LayoutInflater inflater;

    public NearbyListAdapter() {
        inflater = LayoutInflater.from(MyApp.getContext());
    }

    @Override
    public int getCount() {
        return retailers == null? 0: retailers.size();
    }

    @Override
    public Object getItem(int position) {
        return retailers == null? null: retailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return retailers == null? null: retailers.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_nearby_item, null);
        }
        ViewHolder viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                    (TextView) convertView.findViewById(R.id.textViewName),
                    (TextView) convertView.findViewById(R.id.textViewDistance),
                    (TextView) convertView.findViewById(R.id.textViewAddr),
                    (RatingBar) convertView.findViewById(R.id.ratingBar),
                    (TextView) convertView.findViewById(R.id.textViewMark));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Retailer retailer = (Retailer) getItem(position);
        Glide.with(MyApp.getContext()).load(retailer.getPic().getURL()).into(viewHolder.imageView);

        viewHolder.textViewName.setText(retailer.getName());
        viewHolder.textViewDistance.setText(Humanize.with(MyApp.getContext()).distance(
                retailer.getPOI().getDistance()));
        viewHolder.textViewAddr.setText(retailer.getPOI().getAddr());
        viewHolder.ratingBar.setRating(retailer.getRating());
        viewHolder.textViewMark.setText(Character.toString((char) (position + 65)));
        return convertView;
    }

    public void setRetailers(List<Retailer> retailers) {
        this.retailers = retailers;
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewDistance;
        TextView textViewAddr;
        RatingBar ratingBar;
        TextView textViewMark;

        ViewHolder(ImageView imageView, TextView textViewName, TextView textViewDistance,
                   TextView textViewAddr, RatingBar ratingBar, TextView textViewMark) {
            this.imageView = imageView;
            this.textViewName = textViewName;
            this.textViewDistance = textViewDistance;
            this.textViewAddr = textViewAddr;
            this.ratingBar = ratingBar;
            this.textViewMark = textViewMark;
        }
    }
}
