package com.puzheng.the_genuine;

import android.os.Bundle;

public class SPUListFragmentByCategory extends SPUListFragment {
    private int categoryId;

    public SPUListFragmentByCategory() {

    }

    public SPUListFragmentByCategory setCatetoryId(int catetoryId) {
        this.categoryId = catetoryId;
        return this;
    }

    public static SPUListFragment newInstance(String orderBy, int categoryId) {
        return new SPUListFragmentByCategory().setCatetoryId(categoryId).setOrderBy(orderBy);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetSPUListInterface queryClass = new GetSPUListByCategory(this.getActivity(), categoryId);
        new GetSPUListTask(this, queryClass).execute();
    }
}
