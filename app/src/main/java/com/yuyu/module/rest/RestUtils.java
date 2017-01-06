package com.yuyu.module.rest;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RestUtils {

    public static final String BASE = "http://192.168.2.136/FCM/";

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

    public interface FileUploadService {
        @Multipart
        @POST("test.jsp")
        Observable<Void> upload(@Part("message") RequestBody message,
                                        @Part MultipartBody.Part file);
    }

}
