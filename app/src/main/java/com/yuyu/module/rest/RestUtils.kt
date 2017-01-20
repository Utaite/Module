package com.yuyu.module.rest

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class RestUtils {

    private val TAG = RestUtils::class.java.simpleName

    companion object {
        private val BASE = "http://192.168.0.1/"
        private val client = Retrofit.Builder()
                .baseUrl(BASE)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxThreadCallAdapter(Schedulers.io(), AndroidSchedulers.mainThread()))
                .build()

        @JvmStatic
        fun getRetrofit(): Retrofit = client
    }

    interface Upload {
        @Multipart
        @POST("test.jsp")
        fun upload(@Part("message") message: String,
                   @Part body: MultipartBody.Part): Observable<Void>
    }

    interface Login {
        @FormUrlEncoded
        @POST("api/login")
        fun login(@Field("id") id: String,
                  @Field("pw") pw: String): Observable<Int>
    }

}

/*
package com.yuyu.module.rest;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RestUtils {

    public static final String BASE = "http://192.168.0.1/";

    private static Retrofit retrofit;
    private static Retrofit retrofitSSL;

    private static final String TAG = RestUtils.class.getSimpleName();

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE)
                    .client(new OkHttpClient.Builder().build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(new RxThreadCallAdapter(Schedulers.io(), AndroidSchedulers.mainThread()))
            .build();
        }

        return retrofit;
    }

    public static Retrofit getRetrofitSSL(Context context) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        if (retrofitSSL == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE)
                    .client(new OkHttpClient.Builder()
                            .sslSocketFactory(getSSLConfig(context).getSocketFactory())
                            .build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(new RxThreadCallAdapter(Schedulers.io(), AndroidSchedulers.mainThread()))
                    .build();
        }

        return retrofitSSL;
    }

    public interface Upload {
        @Multipart
        @POST("test.jsp")
        Observable<Void> upload(@Part("message") String message,
        @Part MultipartBody.Part body);
    }

    public interface Login {
        @FormUrlEncoded
        @POST("api/login")
        Observable<Integer> login(@Field("id") String id,
        @Field("pw") String pw);
    }

    public static SSLContext getSSLConfig(Context context) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        BufferedInputStream bis = new BufferedInputStream(context.getResources().openRawResource(R.raw.your_certificate));
        Certificate certificate = cf.generateCertificate(bis);
        bis.close();

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("certificate", certificate);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }

}
*/
