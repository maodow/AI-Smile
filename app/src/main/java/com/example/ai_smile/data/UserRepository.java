package com.example.ai_smile.data;

import android.net.Uri;

import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.TestRecordBean;
import com.example.ai_smile.data.bean.TestResultBean;
import com.example.ai_smile.data.bean.UserInfo;
import com.example.ai_smile.login.LoginRegRequestValue;

import java.util.List;

import rx.Observable;

/**
 * Created by Admin yao on 2019/4/24.
 *
 */

public interface UserRepository {

    /**
     * 账号密码登录
     */
    Observable<HttpRespose<UserInfo>> login(LoginRegRequestValue loginRegRequestValue);


    /**
     * 人脸检测
     */
    Observable<HttpRespose<List<TestResultBean>>> faceTest(String macAddress, String path);


    /**
     * 人脸检测记录
     */
    Observable<HttpRespose<List<TestRecordBean>>> getTestRecordData(String macAddress);


    /**
     * 人脸检测详情
     */
    Observable<HttpRespose<List<TestResultBean>>> getRecordInfo(String recordId);


    /**
     * 退出登录
     */
    Observable<HttpRespose> loginOut(LoginRegRequestValue loginRegRequestValue);

}
