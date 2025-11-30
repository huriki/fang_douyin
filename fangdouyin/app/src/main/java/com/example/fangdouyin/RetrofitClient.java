package com.example.fangdouyin;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //测试电脑wif连接不好设置固定ip，故重启服务器后，需重新设置连接目标服务器接口
    private static final String BASE_URL = "http://192.168.8.218:20000";

    private static Retrofit retrofit;
    private static ItemCardApiService apiService;
    // 单例初始化 Retrofit
    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // 服务端基础地址
                    .addConverterFactory(GsonConverterFactory.create()) // Gson 自动解析 JSON
                    .build();
        }
        return retrofit;
    }

    public static ItemCardApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofitInstance().create(ItemCardApiService.class);
        }
        return apiService;
    }
}
