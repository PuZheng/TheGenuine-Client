package com.puzheng.the_genuine.utils;

import android.util.Pair;

import com.puzheng.the_genuine.MyApp;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-06.
 */
public class HttpUtil {
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    private static final int DEAFULT_TIME_OUT_MILLSECONDS = 10000;

    public static String composeUrl(String blueprint, String path) {
        return composeUrl(blueprint, path, null);
    }

    public static String composeUrl(String blueprint, String path, Map<String, String> params) {
        return composeUrl(blueprint, path, params, HTTP);
    }

    public static String composeUrl(String blueprint, String path, Map<String, String> params, String protocol) {
        Pair<String, Integer> pair = MyApp.getServerAddress();
        StringBuilder ret = new StringBuilder();
        ret.append(String.format("%s%s:%d/%s/%s", protocol, pair.first, pair.second, blueprint, path));
        if (params != null) {
            if (MyApp.getCurrentUser() != null) {
                params.put("auth_token", MyApp.getCurrentUser().getToken());
            }
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                ret.append(first ? "?" : "&").append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
        } else {
            if (MyApp.getCurrentUser() != null) {
                ret.append("?auth_token=").append(MyApp.getCurrentUser().getToken());
            }
        }
        return ret.toString();
    }

    public static HttpResponse get(String url) throws IOException {
        return sendRequest(url, "GET", null);
    }

    public static String getStringResult(String url) throws IOException {
        HttpResponse response = get(url);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } else {
            return null;
        }
    }

    public static HttpResponse sendRequest(String url, String method, String data)
            throws IOException {

        HttpResponse response = null;
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, DEAFULT_TIME_OUT_MILLSECONDS);
        HttpConnectionParams.setSoTimeout(params, DEAFULT_TIME_OUT_MILLSECONDS);
        if (method.equals("GET")) {
            HttpGet hg = new HttpGet(url);
            response = new DefaultHttpClient(params).execute(hg);
        } else if (method.equals("POST")) {
            HttpPost hp = new HttpPost(url);
            if (data != null) {
                hp.setHeader("Content-type", "application/json");
                hp.setEntity(new StringEntity(data, "utf-8"));
            }
            response = new DefaultHttpClient(params).execute(hp);
        } else if (method.equals("PUT")) {
            HttpPut hp = new HttpPut(url);
            if (data != null) {
                hp.setHeader("Content-type", "application/json");
                hp.setEntity(new StringEntity(data, "utf-8"));
            }
            response = new DefaultHttpClient(params).execute(hp);
        }
        return response;
    }
}
