package com.example.ai_smile.http;

import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by Admin on 2019/4/24.
 *
 */

public class JsonParamsInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        Request.Builder requestBuilder = request.newBuilder();
        // 捕获JSON 请求
        if(request.body() instanceof FormBody && request.method().equals("REPORT")){
            requestBuilder = transformJsonParams(requestBuilder, (FormBody) request.body());
            request = requestBuilder.build();
        }

        return chain.proceed(request);
    }

    private Request.Builder transformJsonParams(Request.Builder newBuilder, FormBody formBody){
        final JsonObject jsonObject =  new JsonObject();
        //遍历Form参数
        for (int i = 0; i < formBody.size(); i++) {
            jsonObject.addProperty(formBody.name(i), formBody.value(i));
        }
        //构建json params
        return newBuilder.post(new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/json;charset=UTF-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.buffer().writeUtf8(jsonObject.toString());
            }
        });
    }
}
