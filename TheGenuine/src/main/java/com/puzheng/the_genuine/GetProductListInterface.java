package com.puzheng.the_genuine;

import com.puzheng.the_genuine.data_structure.Recommendation;

import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-05.
 */
public interface GetProductListInterface {
        List<Recommendation> getProductList(int sortIdx);
    }
