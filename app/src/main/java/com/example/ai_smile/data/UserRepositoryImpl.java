package com.example.ai_smile.data;

import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.TestRecordBean;
import com.example.ai_smile.data.bean.TestResultBean;
import com.example.ai_smile.data.bean.UserInfo;
import com.example.ai_smile.login.LoginRegRequestValue;
import java.io.File;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * Created by Admin yao on 2019/4/24.
 *
 */
 public class UserRepositoryImpl implements UserRepository {

    private UserApi userApi;

    public UserRepositoryImpl(UserApi userApi) {
        this.userApi = userApi;
    }


    @Override
    public Observable<HttpRespose<UserInfo>> login(LoginRegRequestValue loginRegRequestValue) {
        return userApi.login("17795682997", "123abc");
    }

    @Override
    public Observable<HttpRespose<List<TestResultBean>>> faceTest(String macAddress, String path) {
        return userApi.faceTest(macAddress, prepareFilePart("image", path));
    }

    @Override
    public Observable<HttpRespose<List<TestRecordBean>>> getTestRecordData(String macAddress) {
        return userApi.getTestRecordData(macAddress);
    }

    @Override
    public Observable<HttpRespose<List<TestResultBean>>> getRecordInfo(String recordId) {
        return userApi.getRecordInfo(recordId);
    }

    @Override
    public Observable<HttpRespose> loginOut(LoginRegRequestValue loginRegRequestValue) {
        return userApi.loginOut(loginRegRequestValue.getAccount(), loginRegRequestValue.getPassword());
    }

    private MultipartBody.Part prepareFilePart(String partName, String path) {
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);
    }

}
