package com.example.ai_smile.test;

import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.TestRecordBean;
import com.example.ai_smile.data.bean.TestResultBean;
import com.example.ai_smile.interfaces.ActivityHelperView;
import com.example.ai_smile.interfaces.ActivityHintView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

public interface TestRecordContract {

    interface View extends ActivityHintView, MvpView, ActivityHelperView {
        void onGetTestRecordListSuccess(HttpRespose<List<TestRecordBean>> httpRespose);
        void onGetTestRecordInfoSuccess(HttpRespose<List<TestResultBean>> httpRespose);
    }

    interface Presenter extends MvpPresenter<View> {
        void getTestRecordData(String macAddress);//获取检测记录列表数据
        void getTestRecordInfo(String recordId);//检测记录详情
    }

}
