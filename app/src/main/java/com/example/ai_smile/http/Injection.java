package com.example.ai_smile.http;

import com.example.ai_smile.data.UserApi;
import com.example.ai_smile.data.UserRepository;
import com.example.ai_smile.data.UserRepositoryImpl;
import com.example.ai_smile.login.LoginUseCase;
import com.example.ai_smile.test.FaceTestUseCase;
import com.example.ai_smile.test.TestInfoUseCase;
import com.example.ai_smile.test.TestRecordUseCase;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import static okhttp3.logging.HttpLoggingInterceptor.Logger.DEFAULT;

/**
 * Created by Admin on 2020/5/24.
 *
 */

public class Injection {

    private static final String BASE_URL = "http://47.97.123.163:9096/skintest/";//测试环境
//    private static final String BASE_URL = "http://47.97.123.163:9096/skintest/";//正式环境

    private static OkHttpClient okHttpClient;
    private static UserApi userApi;

    private static boolean isSaveHttpLog = true;

    public static final String USER_AGREEMENT_URL = BASE_URL+"modules/app/user_Agreement.html";//用户协议


    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    //.addInterceptor(new JsonParamsInterceptor())
                    .addInterceptor(new TokenInterceptor())
                    .addInterceptor(new ReRequestInterceptor())
                    .addInterceptor(new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();
        }
        return okHttpClient;
    }

    private static HttpLoggingInterceptor getHttpLoggingInterceptor() {

        HttpLoggingInterceptor.Logger logger;

        if (isSaveHttpLog) {
            logger = new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    //JLog.d(message);
                }
            };
        } else {
            logger = DEFAULT;
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(logger);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    private static Retrofit getRetrofitInstance(String baseUrl) {
        return new Retrofit.Builder().client(Injection.getOkHttpClient()).baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
    }

    /******************************* API *************************/
    public static UserApi provideUserApi() {
        if (userApi == null)
            userApi = getRetrofitInstance(BASE_URL).create(UserApi.class);
        return userApi;
    }


    /******************************* Repository接口 *************************/

    private static UserRepository provideUserRepo() {
        return new UserRepositoryImpl(provideUserApi());
    }


    /****************************** UserCase用例 ******************************/

    public static LoginUseCase provideLoginUseCase() {
        return new LoginUseCase(provideUserRepo());
    }

    public static FaceTestUseCase provideFaceTestUseCase() {
        return new FaceTestUseCase(provideUserRepo());
    }

    public static TestRecordUseCase provideTestRecordUseCase() {
        return new TestRecordUseCase(provideUserRepo());
    }

    public static TestInfoUseCase provideTestInfoUseCase() {
        return new TestInfoUseCase(provideUserRepo());
    }

}
