package com.puzheng.the_genuine.utils;


import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.the_genuine.MyApp;
import com.puzheng.the_genuine.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by xc on 16-1-13.
 */
public class ConfigUtils {
    private static ConfigUtils instance;
    private Config config;

    private ConfigUtils() {
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

    public static synchronized ConfigUtils getInstance() {
        if (instance == null) {
            instance = new ConfigUtils();
        }

        return instance;
    }

    public String getBackend() {
        return config.backend;
    }

    private class Config {
        private String backend;

        private Config(String backend) {
            this.backend = backend;
        }
    }
}
