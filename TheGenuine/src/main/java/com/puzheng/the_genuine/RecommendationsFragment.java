package com.puzheng.the_genuine;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.utils.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xc on 13-11-21.
 */
public class RecommendationsFragment extends ListFragment implements Maskable {


    private static final int NEARYBY = 1;
    private static final int SAME_VENDOR = 2;
    private Context context;
    private int queryType;
    private List<Object> args;
    private View mask;
    private View error;

    private RecommendationsFragment(Context context, int queryType) {
        new RecommendationsFragment(context, queryType, null);
    }

    public RecommendationsFragment(Context context, int queryType, List<Object> args) {
        this.context = context;
        this.queryType = queryType;
        this.args = new ArrayList<Object>();
        if (args != null) {
            this.args.addAll(args);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        new GetRecommendationsTask(this, queryType, args).execute();
    }

    @Override
    public void mask() {
        mask.setVisibility(View.VISIBLE);
        getListView().setVisibility(View.GONE);
        error.setVisibility(View.GONE);
    }

    @Override
    public void unmask(Boolean b) {
        if (b) {
            getListView().setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
        } else {
            getListView().setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
        }
        mask.setVisibility(View.GONE);
    }

    public static RecommendationsFragment createNearByProductsFragment(Context context) {
        return new RecommendationsFragment(context, NEARYBY);
    }

    public static RecommendationsFragment createSameVendorProductsFragment(Context context, int vendorId,
                                                            int productId) {
        List<Object> args = new ArrayList<Object>();
        args.add(vendorId);
        args.add(productId);
        return new RecommendationsFragment(context, SAME_VENDOR, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment_base, container, false);
        mask = rootView.findViewById(R.id.mask);
        error = rootView.findViewById(R.id.error);
        return rootView;
    }

    class GetRecommendationsTask extends AsyncTask<Void, Void, Boolean> {

        private final Maskable maskable;
        private final ListFragment listFragment;
        private final int queryType;
        private final List<Object> args;
        private List<Recommendation> recommendations;

        GetRecommendationsTask(ListFragment listFragment, int queryType, List<Object> args) {
            this.maskable = (Maskable)listFragment;
            this.listFragment = listFragment;
            this.queryType = queryType;
            this.args = new ArrayList<Object>();
            if (args != null) {
                this.args.addAll(args);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                recommendations = WebService.getInstance(getActivity()).getRecommendations(queryType, args);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            maskable.mask();
        }


        @Override
        protected  void onPostExecute(Boolean b) {
            this.maskable.unmask(b);
            listFragment.setListAdapter(new MyRecommendationsAdapter(recommendations));
        }
    }

    class ViewHolder {
        ImageView imageView;
        TextView textViewProductName;
        TextView textViewPrice;
        TextView textViewDistance;
        TextView textViewFavorCnt;
        Button button;
        RatingBar ratingBar;

        ViewHolder(ImageView imageView, TextView textViewProductName, TextView textViewDistance,
                   TextView textViewFavorCnt, TextView textViewPrice, Button button, RatingBar ratingBar) {
            this.imageView = imageView;
            this.textViewProductName = textViewProductName;
            this.textViewDistance = textViewDistance;
            this.textViewFavorCnt = textViewFavorCnt;
            this.textViewPrice = textViewPrice;
            this.button = button;
            this.ratingBar = ratingBar;
        }
    }

    class MyRecommendationsAdapter extends BaseAdapter {

        private final List<Recommendation> recommendations;
        private final LayoutInflater inflater;

        public MyRecommendationsAdapter(List<Recommendation> recommendations) {
            this.recommendations = recommendations;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            return recommendations.get(position).getProductId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.recommendation_list_item, null);
            }
            ViewHolder viewHolder;
            if (convertView.getTag() == null) {
                viewHolder = new ViewHolder((ImageView)convertView.findViewById(R.id.imageView),
                        (TextView)convertView.findViewById(R.id.textViewProductName),
                        (TextView)convertView.findViewById(R.id.textViewDistance),
                        (TextView)convertView.findViewById(R.id.textViewFavorCnt),
                        (TextView)convertView.findViewById(R.id.textViewPrice),
                        (Button)convertView.findViewById(R.id.button),
                        (RatingBar)convertView.findViewById(R.id.ratingBar));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Recommendation recommendation = (Recommendation) getItem(position);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            new GetImageTask(viewHolder.imageView, recommendation.getPicUrl()).execute();

            viewHolder.textViewProductName.setText(recommendation.getProductName());
            viewHolder.textViewDistance.setText(Misc.humanizeDistance(recommendation.getDistance()));
            viewHolder.textViewFavorCnt.setText("人气" + Misc.humanizeFavorCnt(recommendation.getFavorCnt()));
            viewHolder.textViewPrice.setText(String.valueOf(recommendation.getPriceInYuan()));
            viewHolder.ratingBar.setRating(recommendation.getRating());
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NearbyActivity.class);
                    intent.putExtra("current", NearbyActivity.NEARBY_LIST);
                    getActivity().startActivity(intent);
                }
            });
            return convertView;


        }
    }
}
