package com.puzheng.the_genuine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.puzheng.the_genuine.data_structure.Store;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.utils.Misc;

import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-27.
 */
public class NearbyFragment extends ListFragment implements Maskable {
    private View mMask;
    private View mError;

    @Override
    public void mask() {
        mMask.setVisibility(View.VISIBLE);
        getListView().setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment_base, container, false);
        mMask = rootView.findViewById(R.id.mask);
        mError = rootView.findViewById(R.id.error);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // execute task here, otherwise "java.lang.IllegalStateException: Content view not yet created" will
        // generated
        super.onActivityCreated(savedInstanceState);
        new GetNearbyListTask(this).execute();
    }

    @Override
    public void unmask(Boolean b) {
        if (b) {
            getListView().setVisibility(View.VISIBLE);
            mError.setVisibility(View.GONE);
        } else {
            getListView().setVisibility(View.GONE);
            mError.setVisibility(View.VISIBLE);
        }
        mMask.setVisibility(View.GONE);
    }

    class GetNearbyListTask extends AsyncTask<Void, Void, Boolean> {
        private List<Store> mStoreList;

        private Maskable mMaskable;

        GetNearbyListTask(Maskable mMaskable) {
            this.mMaskable = mMaskable;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            this.mMaskable.mask();
            try {
                mStoreList = WebService.getInstance(getActivity()).getNearbyStoreList();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            this.mMaskable.unmask(b);
            NearbyFragment.this.setListAdapter(new NearbyListAdapter(mStoreList));
        }
    }

    class ViewHolder {
        ImageView mImageView;
        TextView mStoreName;
        TextView mDistance;
        TextView mLocation;
        RatingBar mRating;

        ViewHolder(ImageView imageView, TextView nameView, TextView distanceView, TextView locationView, RatingBar ratingBar) {
            this.mImageView = imageView;
            this.mStoreName = nameView;
            this.mDistance = distanceView;
            this.mLocation = locationView;
            this.mRating = ratingBar;
        }
    }

    class NearbyListAdapter extends BaseAdapter {

        private final List<Store> storeList;
        private final LayoutInflater inflater;

        public NearbyListAdapter(List<Store> storeList) {
            this.storeList = storeList;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            return storeList.get(position).getID();
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
                        (TextView) convertView.findViewById(R.id.textViewLocation),
                        (RatingBar) convertView.findViewById(R.id.ratingBar));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Store store = (Store) getItem(position);
            new GetImageTask(viewHolder.mImageView, store.getPicUrl()).execute();

            viewHolder.mStoreName.setText(store.getName());
            viewHolder.mDistance.setText(Misc.humanizeDistance(store.getDistance()));
            viewHolder.mLocation.setText(store.getLocation());
            viewHolder.mRating.setRating(store.getRating());
            return convertView;

        }
    }
}
