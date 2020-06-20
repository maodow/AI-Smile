package com.example.ai_smile.http;

import android.text.TextUtils;
import com.example.ai_smile.data.bean.UserInfo;
import com.example.ai_smile.utils.SpUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.Charset;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Admin on 2019/4/24.
 *
 */

public class ReRequestInterceptor implements Interceptor {

    int RetryCount;

    /* @Override
     public Response intercept(Chain chain) throws IOException {
         Account account = null;
         Request request = chain.request();
         Response response = chain.proceed(request);
         try {
             if (response.code() == 200) {
                 String bodyString = response.body().source().buffer().clone().readString(
                         response.body().contentType().charset(Charset.forName("UTF-8")));
                 JSONObject jsonObject = new JSONObject(bodyString);
                 if (jsonObject.has("code")) {
                     if (jsonObject.getInt("code") == 401) {
                         //{"code":401,"res_msg":"抱歉，您尚未登陆或登陆已过期请重新登陆。"}
                         //退出成功、401错误 清除token
                         SpUtils.getInstance().remove(SpUtils.LOGIN);
                         //response = response.newBuilder().code(401).build();
                         String url = reLoginUrl();
                         if(TextUtils.isEmpty(url)){
                             response = response.newBuilder().code(401).build();
                             return response;
                         }
                         Request newRequest = response.request().newBuilder().url(url).build();
                         Response newResponse = chain.proceed(newRequest);
                         String newBodyString = newResponse.body().source().buffer().clone().readString(
                                 newResponse.body().contentType().charset(Charset.forName("UTF-8")));
                         account = new Gson().fromJson(newBodyString, Account.class);
                         if(account!=null&&!TextUtils.isEmpty(account.getUserId())&&!TextUtils.isEmpty(account.getLogin_token())){
                             SpUtils.getInstance().saveStringToSp(SpUtils.LOGIN,new Gson().toJson(account));
                             request = request.newBuilder()
                                     .addHeader("login_token", account.getLogin_token())
                                     .addHeader("userId", account.getUserId())
                                     .addHeader("Connection", "close")
                                     .build();
                             response = chain.proceed(request);
                         }else{
                             response = response.newBuilder().code(401).build();
                         }
                     }
                 }
             }
         } catch (Throwable e) {

         }
         return response;
     }*/
    @Override
    public Response intercept(Chain chain) throws IOException {
        UserInfo userInfo = null;
        Request request = chain.request();
        Response response = chain.proceed(request);
        try {
            if (response.code() == 401) {
                SpUtils.getInstance().remove(SpUtils.USERINFO);
                //response = response.newBuilder().code(401).build();
                String url = reLoginUrl();
                if (TextUtils.isEmpty(url)) {
                    response = response.newBuilder().code(401).build();
                    return response;
                }
                Request newRequest = response.request().newBuilder().url(url).build();
                Response newResponse = chain.proceed(newRequest);

                String newBodyString = newResponse.body().source().buffer().clone().readString(newResponse.body().contentType().charset(Charset.forName("UTF-8")));

                userInfo = new Gson().fromJson(newBodyString, UserInfo.class);
                if (userInfo != null && !TextUtils.isEmpty(String.valueOf(userInfo.getUserid()))) {
                    SpUtils.getInstance().saveStringToSp(SpUtils.USERINFO, new Gson().toJson(userInfo));
                    request = request.newBuilder()
                            .removeHeader("Connection")
                            .addHeader("Connection", "close")
                            .build();
                    response = chain.proceed(request);
                } else {
                    response = response.newBuilder().code(401).build();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return response;
    }

    public String reLoginUrl() {
        return SpUtils.getInstance().getStringValue(SpUtils.LOGIN_URL, "");
    }

}
