package com.puzheng.the_genuine;

import android.content.Context;
import android.os.AsyncTask;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.BadResponseException;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-05.
 */
class GetProductListByName implements GetProductListInterface {
    private Context context;
    private String query;

    GetProductListByName(Context context, String param) {
        this.context = context;
        this.query = param;
    }

    @Override
    public List<Recommendation> getProductList(String orderBy) throws JSONException, BadResponseException, IOException {
        return WebService.getInstance(this.context).getProductListByName(this.query, orderBy);
    }
}

class GetProductListByCategory implements GetProductListInterface {
    private Context context;
    private int category_id;

    GetProductListByCategory(Context context, int category_id) {
        this.context = context;
        this.category_id = category_id;
    }

    @Override
    public List<Recommendation> getProductList(String orderBy) throws JSONException, BadResponseException, IOException {
        return WebService.getInstance(this.context).getProductListByCategory(this.category_id, orderBy);
    }
}

public class GetProductListTask extends AsyncTask<Void, Void, List<Recommendation>> {
    private ProductListFragment mFragment;
    private GetProductListInterface mGetProductListClass;

    public GetProductListTask(ProductListFragment fragment, GetProductListInterface queryClass) {
        this.mFragment = fragment;
        this.mGetProductListClass = queryClass;
    }

    @Override
    protected List<Recommendation> doInBackground(Void... params) {
        String sortIdx = mFragment.getOrderBy();
        try {
            return this.mGetProductListClass.getProductList(sortIdx);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Recommendation> list) {
        if (list != null) {
            ProductListAdapter listAdapter = new ProductListAdapter(list, mFragment.getActivity());
            mFragment.setListAdapter(listAdapter);
        } else {
            mFragment.setEmptyText(mFragment.getString(R.string.search_no_result_found));
        }
    }
}