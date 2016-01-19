package com.puzheng.the_genuine;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;

import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.the_genuine.adapter.SPUListAdapter;
import com.puzheng.the_genuine.model.Recommendation;
import com.puzheng.the_genuine.model.SPU;

import java.util.List;

class SPUListFragment extends ListFragment {
    private Deferrable<List<SPU>, Pair<String, String>> src;

//    private String orderBy;

    private SPUListFragment() {

    }


//    public SPUListFragment setOrderBy(String orderBy) {
//        this.orderBy = orderBy;
//        return this;
//    }
//
//    public String getOrderBy() {
//        return orderBy;
//    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Recommendation recommendation = (Recommendation) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), SPUActivity.class);
        intent.putExtra(Constants.TAG_SPU_ID, recommendation.getSPUId());
        intent.putExtra(Constants.TAG_SPU_NAME, recommendation.getProductName());
        getActivity().startActivity(intent);
    }

    public SPUListFragment setSrc(Deferrable<List<SPU>,Pair<String,String>> src) {
        this.src = src;
        src.done(new DoneHandler<List<SPU>>() {
            @Override
            public void done(List<SPU> spus) {
                setListAdapter(new SPUListAdapter(spus));
            }
        });
        return this;
    }

    static class Builder {
        private SPUListFragment spuListFragment;
        private Deferrable<List<SPU>, Pair<String, String>> src;

        public Builder() {

        }

        public Builder src(Deferrable<List<SPU>, Pair<String, String>> src) {
            this.src = src;
            return this;
        }

        public SPUListFragment build() {
            return new SPUListFragment().setSrc(src);
        }
    }
}