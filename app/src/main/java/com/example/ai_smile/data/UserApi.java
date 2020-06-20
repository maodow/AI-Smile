package com.example.ai_smile.data;

import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.TestRecordBean;
import com.example.ai_smile.data.bean.TestResultBean;
import com.example.ai_smile.data.bean.UserInfo;
import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Admin on 2020/5/22.
 *
 */

public interface UserApi {

    String LOGIN_API_URL = "app/login/pwdlogin";
    String LOGIN_CODE_API_URL = "app/login/codelogin";

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/login/pwdlogin")
    Observable<HttpRespose<UserInfo>> login(@Query("mobile") String mobile, @Query("pwd") String pwd);


    @Multipart
    @POST("app/yyapi/test")
    Observable<HttpRespose<List<TestResultBean>>> faceTest(@Query("macaddr") String macAddress, @Part MultipartBody.Part partFile);


    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/yyapi/record")
    Observable<HttpRespose<List<TestRecordBean>>> getTestRecordData(@Query("macaddr") String macAddress);


    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/yyapi/testinginfo")
    Observable<HttpRespose<List<TestResultBean>>> getRecordInfo(@Query("id") String recordId);


    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/login/")
    Observable<HttpRespose> loginOut(@Query("mobile") String mobile, @Query("pwd") String pwd);

}
