package com.puzheng.lejian.util;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by xc on 16-1-14.
 */
public class ServiceGenerator {
    private static Retrofit retrofit;

    public static <S> S createService(Class<S> serviceClass) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(ConfigUtil.getInstance().getBackend()).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(serviceClass);
    }
}
