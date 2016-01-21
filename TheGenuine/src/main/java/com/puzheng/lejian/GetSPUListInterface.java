package com.puzheng.lejian;

import com.puzheng.lejian.model.Recommendation;
import com.puzheng.lejian.util.BadResponseException;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-05.
 */
public interface GetSPUListInterface {
    List<Recommendation> getSPUList(String orderBy) throws JSONException, BadResponseException, IOException;
}
