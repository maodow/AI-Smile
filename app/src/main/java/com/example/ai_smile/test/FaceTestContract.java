package com.example.ai_smile.test;

import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.TestResultBean;
import com.example.ai_smile.interfaces.ActivityHelperView;
import com.example.ai_smile.interfaces.ActivityHintView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import java.util.List;

public interface FaceTestContract {

    interface View extends ActivityHintView, MvpView, ActivityHelperView {
        void onFaceTestSuccess(HttpRespose<List<TestResultBean>> testResults);
    }

    interface Presenter extends MvpPresenter<View> {
        void faceTest(String macAddress, String filePath);
    }

}
