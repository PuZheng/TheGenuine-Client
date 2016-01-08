package com.puzheng.the_genuine;

import android.os.Bundle;

public class ProductListFragmentByCategory extends ProductListFragment {
    private int categoryId;

    public ProductListFragmentByCategory() {

    }

    public ProductListFragmentByCategory setCatetoryId(int catetoryId) {
        this.categoryId = catetoryId;
        return this;
    }

    public static ProductListFragment newInstance(String orderBy, int categoryId) {
        return new ProductListFragmentByCategory().setCatetoryId(categoryId).setOrderBy(orderBy);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetSPUListInterface queryClass = new GetSPUListByCategory(this.getActivity(), categoryId);
        new GetSPUListTask(this, queryClass).execute();
    }
}
