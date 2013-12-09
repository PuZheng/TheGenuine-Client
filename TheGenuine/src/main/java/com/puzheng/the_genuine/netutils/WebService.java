package com.puzheng.the_genuine.netutils;

import android.content.Context;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.puzheng.the_genuine.MyApp;
import com.puzheng.the_genuine.data_structure.Category;
import com.puzheng.the_genuine.data_structure.Comment;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.data_structure.Store;
import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.data_structure.VerificationInfo;
import com.puzheng.the_genuine.utils.HTTPUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xc on 13-11-20.
 */
public class WebService {
    private static WebService instance;
    private Context context;

    private WebService(Context c) {
        this.context = c;
    }

    public static WebService getInstance(Context c) {
        if (instance == null) {
            instance = new WebService(c);
        }
        return instance;
    }

    public boolean addComment(int mProductID, String comment, float rating) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    public List<Category> getCategories() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Category> ret = new ArrayList<Category>();
        for (int i = 0; i < 10; ++i) {
            ret.add(new Category(i, "香烟", 100, "http://t1.baidu.com/it/u=2048806342,2776893942&fm=21&gp=0.jpg"));
        }
        return ret;
    }

    public List<Comment> getComments(int productId) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Comment> ret = new ArrayList<Comment>();
        for (int i = 0; i < 100; ++i) {
            ret.add(new Comment(1, 1, "张三",
                    "http://c.hiphotos.baidu.com/image/w%3D230/sign=d882f8216963f6241c5d3e00b745eb32/b3b7d0a20cf431ade17230214936acaf2edd9801.jpg",
                    "just so so", new Date(1384935011000L), 4.5F));
        }
        return ret;
    }

    public List<Store> getNearbyStoreList() {
        List<Store> result = new ArrayList<Store>();
        Store s1 = new Store(1, "麦当劳", "http://pica.nipic.com/2008-05-24/2008524155151338_2.jpg", 4.5f, "A区B座C栋D号", 5000);
        result.add(s1);
        Store s2 = new Store(3, "肯德基", "http://upload.northnews.cn/2013/0723/1374545598503.jpg", 4f, "F区E座D栋C号", 2000);
        result.add(s2);
        return result;
    }

    public List<Recommendation> getProductListByCategory(int category_id, int sortIdx) {
        List<Recommendation> ret = new ArrayList<Recommendation>();
        ret.add(new Recommendation(1, "五粮液", 1000, 120,
                "http://www.vatsliquor.com/UploadFile/images/01.jpg", 4, 500));
        ret.add(new Recommendation(2, "五粮液", 100, 120,
                "http://www.vatsliquor.com/UploadFile/images/01.jpg", 4, 5000));
        ret.add(new Recommendation(3, "五粮液", 500, 120,
                "http://www.vatsliquor.com/UploadFile/images/01.jpg", 4, 100));
        sort(ret, sortIdx);
        return ret;
    }

    public List<Recommendation> getProductListByName(String query, int sortIdx) {
        List<Recommendation> ret = new ArrayList<Recommendation>();
        ret.add(new Recommendation(1, "五粮液2", 1000, 120,
                "http://www.vatsliquor.com/UploadFile/images/01.jpg", 4, 500));
        ret.add(new Recommendation(2, "五粮液1", 100, 120,
                "http://www.vatsliquor.com/UploadFile/images/01.jpg", 4, 5000));
        ret.add(new Recommendation(3, "五粮液23", 500, 120,
                "http://www.vatsliquor.com/UploadFile/images/01.jpg", 4, 100));
        sort(ret, sortIdx);
        return ret;
    }

    public List<Recommendation> getRecommendations(String queryType, int productId) throws IOException, JSONException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("spu_id", String.valueOf(productId));
        params.put("kind", queryType);
        Pair<Float, Float> location = MyApp.getLocation();
        params.put("longitude", String.valueOf(location.first));
        params.put("latitude", String.valueOf(location.second));
        String url = HTTPUtil.composeUrl("rcmd-ws", "rcmd-list", params);
        String result = HTTPUtil.getStringResult(url);
        JSONObject object = new JSONObject(result);

        Type type = new TypeToken<List<Recommendation>>() {
        }.getType();
        Gson gson = new Gson();
        List<Recommendation> ret = gson.fromJson(object.getString("data"), type);
        return ret;
    }

    public InputStream getStreamFromUrl(String sUrl) throws IOException {
        URL url;
        if (sUrl.toLowerCase().startsWith(HTTPUtil.HTTP) || sUrl.toLowerCase().startsWith(HTTPUtil.HTTPS)) {
            url = new URL(sUrl);
        } else {
            Pair<String, Integer> serverAddress = MyApp.getServerAddress();
            StringBuilder target = new StringBuilder(HTTPUtil.HTTP);
            target.append(serverAddress.first).append(":").append(serverAddress.second);
            if (sUrl.startsWith("/")) {
                target.append(sUrl);
            }else {
                target.append("/").append(sUrl);
            }

            url = new URL(target.toString());
        }
        return url.openStream();
    }


    public Pair<User, Boolean> register_or_login(String mEmail, String mPassword) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Pair.create(new User(1, "张三", "asdflkjlkjasdf"), false);
    }

    public VerificationInfo verify(String code, float longitude, float latitude) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        String url = HTTPUtil.composeUrl("tag", "tag/" + code, params);
        String result = HTTPUtil.getStringResult(url);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        return gson.fromJson(result, VerificationInfo.class);
    }

    private void sort(List<Recommendation> list, final int idx) {
        Comparator<Recommendation> comparator = new Comparator<Recommendation>() {
            @Override
            public int compare(Recommendation lhs, Recommendation rhs) {
                switch (idx) {
                    case 0:
                        return lhs.getPriceInYuan() - rhs.getPriceInYuan();
                    case 1:
                        return lhs.getDistance() - rhs.getDistance();
                    case 2:
                        return lhs.getProductName().compareTo(rhs.getProductName());
                    default:
                        return 0;
                }

            }
        };
        Collections.sort(list, comparator);
    }
}
