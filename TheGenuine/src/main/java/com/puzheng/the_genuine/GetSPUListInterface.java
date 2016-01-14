package com.puzheng.the_genuine;

import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.util.BadResponseException;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-05.
 */
public interface GetSPUListInterface {
    List<Recommendation> getSPUList(String orderBy) throws JSONException, BadResponseException, IOException;
}
