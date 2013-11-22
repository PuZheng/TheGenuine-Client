package com.puzheng.the_genuine;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

/**
 * Created by xc on 13-11-22.
 */
public class ProductsFragment extends ListFragment {
    public static Fragment createNearByProductsFragment(Context context) {
        return new ProductsFragment();
    }

    public static Fragment createSameVendorProductsFragment(Context productActivity, int vendorId, int id) {
        return new ProductsFragment();
    }
}
