package com.example.ai_smile.login;

import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.interfaces.ActivityHelperView;
import com.example.ai_smile.interfaces.ActivityHintView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

public interface LoginContract {

    interface View extends ActivityHintView, MvpView, ActivityHelperView {
        void onLoginSuccess(HttpRespose httpRespose);
        void onLoginFail();
        void usernameRequestFocus();
        void passwordRequestFocus();
        void verifyRequestFocus();
    }

    interface Presenter extends MvpPresenter<View> {
        void login(String username, String password);
    }

}
