package com.puzheng.the_genuine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.utils.Misc;

import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-04.
 */
class ViewHolder {
    ImageView imageView;
    TextView textViewProductName;
    TextView textViewPrice;
    TextView textViewDistance;
    RatingBar ratingBar;

    ViewHolder(ImageView imageView, TextView textViewProductName, TextView textViewDistance, TextView textViewPrice, RatingBar ratingBar) {
        this.imageView = imageView;
        this.textViewProductName = textViewProductName;
        this.textViewDistance = textViewDistance;
        this.textViewPrice = textViewPrice;
        this.ratingBar = ratingBar;
    }
}

public class ProductListAdapter extends BaseAdapter {
    private List<Recommendation> mProductList;
    private LayoutInflater inflater;

    public ProductListAdapter(List<Recommendation> list, Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mProductList = list;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mProductList.get(position).getSpuId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.product_list_item, null);
        }
        ViewHolder viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                    (TextView) convertView.findViewById(R.id.textViewProductName),
                    (TextView) convertView.findViewById(R.id.textViewDistance),
                    (TextView) convertView.findViewById(R.id.textViewPrice),
                    (RatingBar) convertView.findViewById(R.id.ratingBar));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Recommendation recommendation = (Recommendation) getItem(position);
        new GetImageTask(viewHolder.imageView, recommendation.getPicUrl()).execute();

        viewHolder.textViewProductName.setText(recommendation.getProductName());
        viewHolder.textViewDistance.setText(Misc.humanizeDistance(recommendation.getDistance()));
        viewHolder.textViewPrice.setText("￥" + recommendation.getPriceInYuan());
        viewHolder.ratingBar.setRating(recommendation.getRating());
        return convertView;
    }
}
