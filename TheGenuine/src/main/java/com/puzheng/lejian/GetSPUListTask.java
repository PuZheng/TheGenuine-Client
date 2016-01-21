package com.puzheng.lejian;

import android.content.Context;
import android.os.AsyncTask;

import com.puzheng.lejian.model.Recommendation;
import com.puzheng.lejian.netutils.WebService;
import com.puzheng.lejian.util.BadResponseException;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-05.
 */
class GetSPUListByName implements GetSPUListInterface {
    private Context context;
    private String query;

    GetSPUListByName(Context context, String param) {
        this.context = context;
        this.query = param;
    }

    @Override
    public List<Recommendation> getSPUList(String orderBy) throws JSONException, BadResponseException, IOException {
        return WebService.getInstance(this.context).getSPUListByName(this.query, orderBy);
    }
}

class GetSPUListByCategory implements GetSPUListInterface {
    private Context context;
    private int category_id;

    GetSPUListByCategory(Context context, int category_id) {
        this.context = context;
        this.category_id = category_id;
    }

    @Override
    public List<Recommendation> getSPUList(String orderBy) throws JSONException, BadResponseException, IOException {
        return WebService.getInstance(this.context).getSPUListByCategory(this.category_id, orderBy);
    }
}

public class GetSPUListTask extends AsyncTask<Void, Void, List<Recommendation>> {
    private SPUListFragment mFragment;
    private GetSPUListInterface mGetProductListClass;

    public GetSPUListTask(SPUListFragment fragment, GetSPUListInterface queryClass) {
        this.mFragment = fragment;
        this.mGetProductListClass = queryClass;
    }

    @Override
    protected List<Recommendation> doInBackground(Void... params) {
//        String sortIdx = mFragment.getOrderBy();
//        try {
//            return this.mGetProductListClass.getSPUList(sortIdx);
//        } catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Recommendation> list) {
        if (list != null && !list.isEmpty()) {
            RecommendationListAdapter listAdapter = new RecommendationListAdapter(list, mFragment.getActivity(),
                    (ImageFetcherInteface) mFragment.getActivity());
            mFragment.setListAdapter(listAdapter);
        } else {
            mFragment.setListAdapter(null);
            mFragment.setEmptyText(mFragment.getString(R.string.search_no_result_found));
        }
    }
}