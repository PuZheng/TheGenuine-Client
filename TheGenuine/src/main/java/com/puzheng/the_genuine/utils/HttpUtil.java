package com.puzheng.the_genuine.utils;

import android.util.Pair;
import com.puzheng.the_genuine.MyApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-06.
 */
public class HttpUtil {
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    private static final String CHARSET = "UTF-8";
    private static final int DEAFULT_CONNECTION_TIME_OUT_MILLSECONDS = 1500;
    private static final int DEAFULT_SO_TIME_OUT_MILLSECONDS = 1000;

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

    public static String getStringResult(String urlString) throws IOException, BadResponseException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept-Charset", CHARSET);
        connection.setConnectTimeout(DEAFULT_CONNECTION_TIME_OUT_MILLSECONDS);
        connection.setReadTimeout(DEAFULT_SO_TIME_OUT_MILLSECONDS);
        try {
            return getResultFromConnection(connection);
        } finally {
            connection.disconnect();
        }
    }

    public static URL getURL(String sUrl) throws MalformedURLException {
        if (sUrl.toLowerCase().startsWith(HTTP) || sUrl.toLowerCase().startsWith(HTTPS)) {
            return new URL(sUrl);
        } else {
            Pair<String, Integer> serverAddress = MyApp.getServerAddress();
            StringBuilder target = new StringBuilder(HTTP);
            target.append(serverAddress.first).append(":").append(serverAddress.second);
            if (sUrl.startsWith("/")) {
                target.append(sUrl);
            } else {
                target.append("/").append(sUrl);
            }

            return new URL(target.toString());
        }
    }

    public static String postStringResult(String urlString) throws IOException, BadResponseException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept-Charset", CHARSET);
        connection.setConnectTimeout(DEAFULT_CONNECTION_TIME_OUT_MILLSECONDS);
        connection.setReadTimeout(DEAFULT_SO_TIME_OUT_MILLSECONDS);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setChunkedStreamingMode(0);
        try {
            return getResultFromConnection(connection);
        } finally {
            connection.disconnect();
        }
    }

    private static String getResultFromConnection(HttpURLConnection connection) throws IOException, BadResponseException {
        int statusCode = connection.getResponseCode();
        if (isSucceed(statusCode)) {
            return readStream(connection.getInputStream());
        } else {
            throw new BadResponseException(statusCode, connection.getURL().getFile(), readStream(connection.getErrorStream()));
        }
    }

    private static boolean isSucceed(int statusCode) {
        return statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED;
    }

    private static String readStream(InputStream in) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        InputStreamReader inr = new InputStreamReader(in, CHARSET);
        BufferedReader reader = new BufferedReader(inr);
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}
