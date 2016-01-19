package com.puzheng.the_genuine;

import android.os.Bundle;

public class SPUListFragmentByName extends SPUListFragment {
    private String query;

    public SPUListFragmentByName() {

    }

    public SPUListFragmentByName setQuery(String query) {
        this.query = query;
        return this;
    }

    public static SPUListFragment newInstance(String orderBy, String query) {
        return new SPUListFragmentByName().setQuery(query).setOrderBy(orderBy);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetSPUListInterface queryClass = new GetSPUListByName(this.getActivity(), query);
        new GetSPUListTask(this, queryClass).execute();
    }
}
