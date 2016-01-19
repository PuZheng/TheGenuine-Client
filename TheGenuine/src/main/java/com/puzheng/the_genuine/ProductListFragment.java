package com.puzheng.the_genuine;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.puzheng.the_genuine.model.Recommendation;

abstract class ProductListFragment extends ListFragment {
    private String orderBy;

    ProductListFragment() {

    }

    public ProductListFragment setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Recommendation recommendation = (Recommendation) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), SPUActivity.class);
        intent.putExtra(Constants.TAG_SPU_ID, recommendation.getSPUId());
        intent.putExtra(Constants.TAG_SPU_NAME, recommendation.getProductName());
        getActivity().startActivity(intent);
    }
}