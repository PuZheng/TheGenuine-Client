package com.puzheng.the_genuine.netutils;

import android.content.Context;

import com.puzheng.the_genuine.data_structure.Recommendation;
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
                "深圳腾飞烟酒专卖", 1, "贵州茅台酒业",  picUrlList, 4.3F, 10, 20, 100);
    }

    public InputStream getStreamFromUrl(String sUrl) throws IOException {
        URL url = new URL(sUrl);
        return url.openStream();
    }

    public List<Recommendation> getRecommendations(int queryType, List<Object> args) {
        List<Recommendation> ret = new ArrayList<Recommendation>();
        ret.add(new Recommendation(2, "五粮液", 100, 120, "http://www.vatsliquor.com/UploadFile/images/01.jpg", 4));
        return ret;
    }
}
