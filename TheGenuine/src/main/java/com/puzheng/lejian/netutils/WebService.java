package com.puzheng.lejian.netutils;

import android.content.Context;

import com.puzheng.lejian.model.Verification;
import com.puzheng.lejian.util.BadResponseException;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by xc on 13-11-20.
 */
public class WebService {
    private static WebService instance;
    private Context context;

    private WebService(Context c) {
        this.context = c;
    }

    public Verification verify(String code) throws IOException, BadResponseException, JSONException {
        return null;
//        String tag = getTag(code);
//        HashMap<String, String> params = null;
//        try {
//            params = getCurrentLocation();
//        } catch (LocateErrorException e) {
//            e.printStackTrace();
//        }
//        String url = HttpUtil.composeUrl("tag-ws", "tag/" + tag, params);
//        try {
//            String result = HttpUtil.getStringResult(url);
//            Gson gson = new GsonBuilder().setDateFormat(Const.TIME_FORMAT).create();
//            return gson.fromJson(result, Verification.class);
//        } catch (BadResponseException e) {
//            if (e.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
//                return null;
//            } else {
//                throw e;
//            }
//        }
    }

    private String getTag(String url) throws BadResponseException, IOException, JSONException {
//        try {
//            if (url.startsWith("http")) {
//                List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), HttpUtil.CHARSET);
//                for (NameValuePair param : params) {
//                    if (param.getName().equals("tag")) {
//                        return param.getValue();
//                    }
//                }
//            } else {
//                return url;
//            }
//        } catch (URISyntaxException e) {
//        }
//        throw new BadResponseException(-1, url, context.getString(R.string.error_barcode));
        return null;
    }
}
