package com.example.ai_smile.http;

import android.util.Log;
import com.example.ai_smile.data.UserApi;
import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.utils.SpUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.Charset;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

/**
 * 为了正常打印token参数, 请先添加该拦截器在添加LoggerInterceptor
 * Created by Admin on 2019/4/24.
 */

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        HttpRespose httpRespose = null;
        Response response = chain.proceed(request);
        if (null != response.body()) {
            String bodyString = response.body().source().buffer().clone().readString(response.body().contentType().charset(Charset.forName("UTF-8")));
            Buffer buffer = new Buffer();
            if (null != request.body()) {
                request.body().writeTo(buffer);
            }
            String reqContent = buffer.readString(Charset.forName("UTF-8"));
            Log.e("------TokenInterceptor", "\nreqUrl: "+url+"\nrequest: "+reqContent+"\nresponse: "+bodyString);
            try {
                if (url.contains(UserApi.LOGIN_API_URL) || url.contains(UserApi.LOGIN_CODE_API_URL) && response.code() == 200) { //登录成功 保存token
                    httpRespose = new Gson().fromJson(bodyString, HttpRespose.class);
                    if (null != httpRespose.getResult()) {
                        if (url.contains(UserApi.LOGIN_CODE_API_URL)) {
                            SpUtils.getInstance().saveStringToSp(SpUtils.LOGIN_URL, url);
                        }
                        SpUtils.getInstance().saveStringToSp(SpUtils.USERINFO, new Gson().toJson(httpRespose.getResult()));
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}
