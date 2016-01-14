package com.puzheng.the_genuine.util;

import com.puzheng.the_genuine.MyApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-06.
 */
public class HttpUtil {
    public static final String CHARSET = "UTF-8";
    private static final int DEAFULT_CONNECTION_TIME_OUT_MILLSECONDS = 1000;
    private static final int DEAFULT_SO_TIME_OUT_MILLSECONDS = 5000;

    public static String composeUrl(String blueprint, String path) {
        return composeUrl(blueprint, path, null);
    }

    public static String composeUrl(String blueprint, String path, Map<String, String> params) {
        String backend = ConfigUtil.getInstance().getBackend();
        StringBuilder ret = new StringBuilder();
        ret.append(String.format("%s/%s/%s", backend, blueprint, path));
        if (params != null) {
            if (MyApp.getCurrentUser() != null) {
                params.put("auth_token", MyApp.getCurrentUser().getJwtToken());
            }
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    ret.append(first ? "?" : "&").append(URLEncoder.encode(entry.getKey(), CHARSET)
                    ).append("=").append(URLEncoder.encode(entry.getValue(), CHARSET));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                first = false;
            }
        } else {
            if (MyApp.getCurrentUser() != null) {
                ret.append("?auth_token=").append(MyApp.getCurrentUser().getJwtToken());
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
        if (sUrl.toLowerCase().startsWith("http") || sUrl.toLowerCase().startsWith("https")) {
            return new URL(sUrl);
        } else {
            StringBuilder target = new StringBuilder(ConfigUtil.getInstance().getBackend());
            if (sUrl.startsWith("/")) {
                target.append(sUrl);
            } else {
                target.append("/").append(sUrl);
            }
            return new URL(target.toString());
        }
    }

    public static String postStringResult(String urlString, HashMap<String, String> params) throws IOException, BadResponseException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept-Charset", CHARSET);
        connection.setConnectTimeout(DEAFULT_CONNECTION_TIME_OUT_MILLSECONDS);
        connection.setReadTimeout(DEAFULT_SO_TIME_OUT_MILLSECONDS);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        if (params != null) {
            connection.setDoOutput(true);
            writer(connection.getOutputStream(), params);
        }
        try {
            return getResultFromConnection(connection);
        } finally {
            connection.disconnect();
        }
    }

    public static String postStringResult(String urlString) throws IOException, BadResponseException {
        return postStringResult(urlString, null);
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

    private static void writer(OutputStream outputStream, HashMap<String, String> params) {
        OutputStreamWriter osw = new OutputStreamWriter(outputStream);
        BufferedWriter writer = new BufferedWriter(osw);
        StringBuilder paramsStr = new StringBuilder();
        boolean first = true;
        for (String s : params.keySet()) {
            if (!first) {
                paramsStr.append("&");
            }
            paramsStr.append(s);
            paramsStr.append("=");
            paramsStr.append(params.get(s));
            first = false;
        }
        try {
            writer.write(paramsStr.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
