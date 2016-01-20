package com.puzheng.the_genuine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.puzheng.the_genuine.model.StoreResponse;
import com.puzheng.the_genuine.image_utils.ImageFetcher;
import com.puzheng.the_genuine.util.Misc;

import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-27.
 */
public class NearbyFragment extends ListFragment {
    private Context mContext;
    private ImageFetcher mImageFetcher;

    public NearbyFragment() {

    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setListAdapter(new NearbyListAdapter(((NearbyActivity) getActivity()).getStoreResponses()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageFetcher = ImageFetcher.getImageFetcher(this.getActivity(), getResources().getDimensionPixelSize(R.dimen.image_view_list_item_width), 0.25f);
    }

    class ViewHolder {
        ImageView mImageView;
        TextView mStoreName;
        TextView mDistance;
        TextView mAddress;
        RatingBar mRating;
        TextView mMarkView;

        ViewHolder(ImageView imageView, TextView nameView, TextView distanceView, TextView addressView, RatingBar ratingBar, TextView markView) {
            this.mImageView = imageView;
            this.mStoreName = nameView;
            this.mDistance = distanceView;
            this.mAddress = addressView;
            this.mRating = ratingBar;
            this.mMarkView = markView;
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
                        (RatingBar) convertView.findViewById(R.id.ratingBar),
                        (TextView) convertView.findViewById(R.id.markView));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            StoreResponse response = (StoreResponse) getItem(position);
            mImageFetcher.loadImage(response.getStore().getIcon(), viewHolder.mImageView);

            viewHolder.mStoreName.setText(response.getStore().getName());
//            viewHolder.mDistance.setText(Misc.humanizeDistance(response.getDistance(), getActivity()));
            viewHolder.mAddress.setText(response.getStore().getAddress());
            viewHolder.mRating.setRating(response.getStore().getRating());
            viewHolder.mMarkView.setText(String.valueOf(position + 1));
            return convertView;
        }
    }
}
