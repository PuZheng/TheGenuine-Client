package com.puzheng.the_genuine;

import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.puzheng.the_genuine.data_structure.StoreResponse;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.utils.Misc;

import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-27.
 */
public class NearbyFragment extends ListFragment {
    private Context mContext;

    public NearbyFragment(Context context, List<StoreResponse> storeList) {
        this.mContext = context;
        setListAdapter(new NearbyListAdapter(storeList));
    }

    class ViewHolder {
        ImageView mImageView;
        TextView mStoreName;
        TextView mDistance;
        TextView mAddress;
        RatingBar mRating;

        ViewHolder(ImageView imageView, TextView nameView, TextView distanceView, TextView addressView, RatingBar ratingBar) {
            this.mImageView = imageView;
            this.mStoreName = nameView;
            this.mDistance = distanceView;
            this.mAddress = addressView;
            this.mRating = ratingBar;
        }
    }

    class NearbyListAdapter extends BaseAdapter {

        private final List<StoreResponse> storeList;
        private final LayoutInflater inflater;

        public NearbyListAdapter(List<StoreResponse> storeList) {
            this.storeList = storeList;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return storeList.size();
        }

        @Override
        public Object getItem(int position) {
            return storeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return storeList.get(position).getStore().getID();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_nearby_item, null);
            }
            ViewHolder viewHolder;
            if (convertView.getTag() == null) {
                viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                        (TextView) convertView.findViewById(R.id.textViewStore),
                        (TextView) convertView.findViewById(R.id.textViewDistance),
                        (TextView) convertView.findViewById(R.id.textViewAddress),
                        (RatingBar) convertView.findViewById(R.id.ratingBar));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            StoreResponse response = (StoreResponse) getItem(position);
            new GetImageTask(viewHolder.mImageView, response.getStore().getIcon()).execute();

            viewHolder.mStoreName.setText(response.getStore().getName());
            viewHolder.mDistance.setText(Misc.humanizeDistance(response.getDistance()));
            viewHolder.mAddress.setText(response.getStore().getAddress());
            viewHolder.mRating.setRating(response.getStore().getRating());
            return convertView;
        }
    }
}
