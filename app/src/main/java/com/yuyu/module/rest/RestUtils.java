package com.yuyu.module.rest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RestUtils {

    public static final String BASE = "";

    private static Retrofit retrofit;

    public static void initialize() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new RxThreadCallAdapter(Schedulers.io(), AndroidSchedulers.mainThread()))
                .build();
    }

    public static Retrofit getRestClient() {
        return retrofit;
    }

    public interface RestBitmap {
    }

}
