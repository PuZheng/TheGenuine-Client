package com.puzheng.lejian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.lejian.MyApp;
import com.puzheng.lejian.R;
import com.puzheng.lejian.model.SPUType;

import java.util.List;

public class SPUTypeListAdapter extends BaseAdapter {

    private final List<SPUType> spuTypes;
    private final LayoutInflater inflater;

    public SPUTypeListAdapter(List<SPUType> spuTypes) {
        this.spuTypes = spuTypes;
        inflater = LayoutInflater.from(MyApp.getContext());
    }

    @Override
    public int getCount() {
        return spuTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return spuTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return spuTypes.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spu_type_grid_item, null);
        }
        ViewHolder viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final SPUType spuType = (SPUType) getItem(position);
        Logger.json(new Gson().toJson(spuType));

        Glide.with(MyApp.getContext()).load(spuType.getPic().getURL())
                .error(R.drawable.ic_broken_image_black_24dp).into(viewHolder.imageView);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;

        ViewHolder(ImageView imageView) {
            this.imageView = imageView;
        }

    }
}
