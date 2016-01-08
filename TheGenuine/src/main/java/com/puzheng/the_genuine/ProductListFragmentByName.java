package com.puzheng.the_genuine;

import android.os.Bundle;

public class ProductListFragmentByName extends ProductListFragment {
    private String query;

    public ProductListFragmentByName() {

    }

    public ProductListFragmentByName setQuery(String query) {
        this.query = query;
        return this;
    }

    public static ProductListFragment newInstance(String orderBy, String query) {
        return new ProductListFragmentByName().setQuery(query).setOrderBy(orderBy);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetSPUListInterface queryClass = new GetSPUListByName(this.getActivity(), query);
        new GetSPUListTask(this, queryClass).execute();
    }
}
