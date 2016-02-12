package com.puzheng.lejian.util;


import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.lejian.MyApp;
import com.puzheng.lejian.R;

import org.stringtemplate.v4.ST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by xc on 16-1-13.
 */
public class ConfigUtil {
    private static ConfigUtil instance;
    private Config config;

    private ConfigUtil() {
        Gson gson = new Gson();
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(MyApp.getContext().getResources().openRawResource(R.raw.config)));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            config = gson.fromJson(sb.toString(), Config.class);
            Logger.i("CONFIG READ");
            Logger.json(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ConfigUtil getInstance() {
        if (instance == null) {
            instance = new ConfigUtil();
        }

        return instance;
    }

    public String getBackend() {
        return config.backend;
    }

    public String getWechatAppId() {
        return config.wechatAppId;
    }

    public String getWechatAppSecret() {
        return config.wechatAppSecret;
    }

    public String getShareURLTemplate() {
        return config.shareURLTemplate;
    }

    private class Config {
        private String backend;
        private String wechatAppId;
        public String wechatAppSecret;
        public String shareURLTemplate;

        private Config(String backend, String wechatAppId, String wechatAppSecret,
                       String shareURLTemplate) {
            this.backend = backend;
            this.wechatAppId = wechatAppId;
            this.wechatAppSecret = wechatAppSecret;
            this.shareURLTemplate = shareURLTemplate;
        }
    }
}
