package com.puzheng.the_genuine.netutils;

import android.content.Context;
import android.location.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.puzheng.the_genuine.Constants;
import com.puzheng.the_genuine.MyApp;
import com.puzheng.the_genuine.data_structure.Category;
import com.puzheng.the_genuine.data_structure.Comment;
import com.puzheng.the_genuine.data_structure.Favor;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.data_structure.SPUResponse;
import com.puzheng.the_genuine.data_structure.StoreResponse;
import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.data_structure.VerificationInfo;
import com.puzheng.the_genuine.utils.BadResponseException;
import com.puzheng.the_genuine.utils.HttpUtil;
import com.puzheng.the_genuine.utils.LocateErrorException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xc on 13-11-20.
 */
public class WebService {
    private static WebService instance;
    private Context context;

    private WebService(Context c) {
        this.context = c;
    }

    public void addComment(int spu_id, String comment, float rating) throws IOException, BadResponseException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("spu_id", String.valueOf(spu_id));
        params.put("content", comment);
        params.put("rating", String.valueOf(rating));
        String url = HttpUtil.composeUrl("comment-ws", "comment", params);
        HttpUtil.postStringResult(url);
    }

    public boolean addFavor(int spu_id) throws IOException, BadResponseException {
        String url = HttpUtil.composeUrl("favor-ws", "favor/" + spu_id);
        HttpUtil.postStringResult(url);
        return true;
    }

    public boolean denounce(String tag, String reason) throws IOException, BadResponseException {
        HashMap<String, String> params = null;
        try {
            params = getCurrentLocation();
        } catch (LocateErrorException e) {
            params = new HashMap<String, String>();
        }
        params.put("reason", reason.trim());
        String url = HttpUtil.composeUrl("tag-ws", "tag-denounce/" + tag.trim(), params);
        HttpUtil.postStringResult(url);
        return true;
    }

    public List<Category> getCategories() throws IOException, JSONException, BadResponseException {
        String url = HttpUtil.composeUrl("spu-ws", "spu-type-list");
        String result = HttpUtil.getStringResult(url);
        JSONObject object = new JSONObject(result);
        Type type = new TypeToken<List<Category>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(object.getString("data"), type);
    }

    public List<Comment> getComments(int spuId) throws IOException, JSONException, BadResponseException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("spu_id", String.valueOf(spuId));
        String url = HttpUtil.composeUrl("comment-ws", "comment-list", params);
        String result = HttpUtil.getStringResult(url);
        JSONObject jsonObject = new JSONObject(result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Type type = new TypeToken<List<Comment>>() {
        }.getType();
        return gson.fromJson(jsonObject.getString("data"), type);
    }

    public HashMap<String, List<Favor>> getFavorCategories() throws IOException, JSONException, BadResponseException {
        HashMap<String, String> currentLocation = null;
        try {
            currentLocation = getCurrentLocation();
        } catch (LocateErrorException e) {
            e.printStackTrace();
        }
        String url = HttpUtil.composeUrl("favor-ws", "favors", currentLocation);
        String result = HttpUtil.getStringResult(url);
        HashMap<String, List<Favor>> ret = new HashMap<String, List<Favor>>();
        Type type = new TypeToken<List<Favor>>() {
        }.getType();
        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();

        JSONObject jsonObject = new JSONObject(result);
        Iterator iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            List<Favor> list = gson.fromJson(jsonObject.getString(key), type);
            ret.put(key, list);
        }
        return ret;
    }

    public static WebService getInstance(Context c) {
        if (instance == null) {
            instance = new WebService(c);
        }
        return instance;
    }

    public List<StoreResponse> getNearbyStoreList(int spu_id) throws IOException, JSONException, BadResponseException, LocateErrorException {
        HashMap<String, String> params = getCurrentLocation();
        if (spu_id != Constants.INVALID_ARGUMENT) {
            params.put("spu_id", String.valueOf(spu_id));
        }
        String url = HttpUtil.composeUrl("retailer-ws", "retailer-list", params);
        String result = HttpUtil.getStringResult(url);
        JSONObject object = new JSONObject(result);
        Type type = new TypeToken<List<StoreResponse>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(object.getString("data"), type);
    }

    public List<Recommendation> getRecommendations(String queryType, int productId) throws IOException, JSONException, BadResponseException {
        HashMap<String, String> params;
        try {
            params = getCurrentLocation();
        } catch (LocateErrorException e) {
            e.printStackTrace();
            params = new HashMap<String, String>();
        }
        params.put("spu_id", String.valueOf(productId));
        params.put("kind", queryType);
        String url = HttpUtil.composeUrl("rcmd-ws", "rcmd-list", params);
        String result = HttpUtil.getStringResult(url);
        JSONObject object = new JSONObject(result);
        Type type = new TypeToken<List<Recommendation>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(object.getString("data"), type);
    }

    public List<Recommendation> getSPUListByCategory(int category_id, String orderBy) throws IOException, JSONException, BadResponseException {
        HashMap<String, String> params;
        try {
            params = getCurrentLocation();
        } catch (LocateErrorException e) {
            params = new HashMap<String, String>();
        }
        params.put("spu_type_id", String.valueOf(category_id));
        params.put("order_by", orderBy);
        return getSPUList(params);
    }

    public List<Recommendation> getSPUListByName(String query, String orderBy) throws IOException, JSONException, BadResponseException {
        HashMap<String, String> params;
        try {
            params = getCurrentLocation();
        } catch (LocateErrorException e) {
            params = new HashMap<String, String>();
        }
        params.put("kw", query);
        params.put("order_by", orderBy);
        return getSPUList(params);
    }

    public SPUResponse getSPUResponse(int spu_id) throws IOException, BadResponseException {
        HashMap<String, String> currentLocation = null;
        try {
            currentLocation = getCurrentLocation();
        } catch (LocateErrorException e) {
            e.printStackTrace();
        }
        String url = HttpUtil.composeUrl("spu-ws", "spu/" + spu_id, currentLocation);
        String result = HttpUtil.getStringResult(url);
        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
        return gson.fromJson(result, SPUResponse.class);
    }

    public User login(String email, String password) throws IOException, BadResponseException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", email);
        map.put("password", password);
        String url = HttpUtil.composeUrl("user-ws", "login", map);
        String result = HttpUtil.postStringResult(url);
        Gson gson = new Gson();
        return gson.fromJson(result, User.class);
    }

    public User register(String email, String password) throws IOException, BadResponseException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", email);
        params.put("password", password);
        String url = HttpUtil.composeUrl("user-ws", "register", params);
        String result = HttpUtil.postStringResult(url);
        Gson gson = new Gson();
        return gson.fromJson(result, User.class);
    }

    public VerificationInfo verify(String code) throws IOException, BadResponseException, JSONException {
        String tag = getTag(code);
        HashMap<String, String> params = null;
        try {
            params = getCurrentLocation();
        } catch (LocateErrorException e) {
            e.printStackTrace();
        }
        String url = HttpUtil.composeUrl("tag-ws", "tag/" + tag, params);
        try {
            String result = HttpUtil.getStringResult(url);
            Gson gson = new GsonBuilder().setDateFormat(Constants.TIME_FORMAT).create();
            return gson.fromJson(result, VerificationInfo.class);
        } catch (BadResponseException e) {
            if (e.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private HashMap<String, String> getCurrentLocation() throws LocateErrorException {
        HashMap<String, String> params = new HashMap<String, String>();
        Location location = MyApp.getLocation();
        params.put("longitude", String.valueOf(location.getLongitude()));
        params.put("latitude", String.valueOf(location.getLatitude()));
        return params;
    }

    private List<Recommendation> getSPUList(HashMap<String, String> params) throws IOException, JSONException, BadResponseException {
        String url = HttpUtil.composeUrl("spu-ws", "spu-list", params);
        String result = HttpUtil.getStringResult(url);
        JSONObject object = new JSONObject(result);
        Type type = new TypeToken<List<Recommendation>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(object.getString("data"), type);
    }

    private String getTag(String url) throws BadResponseException, IOException, JSONException {
        try {
            if (url.startsWith("http")) {
                List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), HttpUtil.CHARSET);
                for (NameValuePair param : params) {
                    if (param.getName().equals("tag")) {
                        return param.getValue();
                    }
                }
            } else {
                return url;
            }
        } catch (URISyntaxException e) {
        }
        throw new BadResponseException(-1, url, "条码有误，请重试！");
    }
}
