package com.puzheng.the_genuine.netutils;

import android.content.Context;

import com.puzheng.the_genuine.data_structure.Category;
import com.puzheng.the_genuine.data_structure.Comment;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.data_structure.Store;
import com.puzheng.the_genuine.data_structure.VerificationInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
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

    public static WebService getInstance(Context c) {
        if (instance == null) {
            instance = new WebService(c);
        }
        return instance;
    }

    public List<Store> getNearbyStoreList() {
        List<Store> result = new ArrayList<Store>();
        Store s1 = new Store(1, "麦当劳", "http://pica.nipic.com/2008-05-24/2008524155151338_2.jpg", 4.5f, "A区B座C栋D号", 5000);
        result.add(s1);
        Store s2 = new Store(3, "肯德基", "http://upload.northnews.cn/2013/0723/1374545598503.jpg", 4f, "F区E座D栋C号", 2000);
        result.add(s2);
        return result;
    }

    public List<Recommendation> getRecommendations(int queryType, List<Object> args) {
        List<Recommendation> ret = new ArrayList<Recommendation>();
        ret.add(new Recommendation(2, "五粮液", 100, 120,
                "http://www.vatsliquor.com/UploadFile/images/01.jpg", 4, 500));
        ret.add(new Recommendation(2, "五粮液", 100, 120,
                "http://www.vatsliquor.com/UploadFile/images/01.jpg", 4, 500));
        return ret;
    }

    public InputStream getStreamFromUrl(String sUrl) throws IOException {
        URL url = new URL(sUrl);
        return url.openStream();
    }

    public VerificationInfo verify(String code) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> picUrlList = new ArrayList<String>();
        picUrlList.add("http://pmgs.kongfz.com/data/pre_show_pic/78/236/2490.jpg");
        picUrlList.add("http://t1.baidu.com/it/u=1193376269,1094496181&fm=21&gp=0.jpg");
        return new VerificationInfo(1, "茅台酒", "123456", new Date(1384935011000L), new Date(1385935011000L),
                1, "贵州茅台酒业", picUrlList, 4.5F, 10, 20, 100);
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
}
