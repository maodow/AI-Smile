package com.example.ai_smile.login;

import com.example.ai_smile.R;
import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.http.ErrorMessage;
import com.example.ai_smile.http.ThrowableToErrorMessage;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import rx.Subscriber;

public class LoginPresenter extends MvpNullObjectBasePresenter<LoginContract.View> implements LoginContract.Presenter {

    private LoginUseCase loginUseCase;


    public LoginPresenter(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        loginUseCase.unSubscribe();
    }

    @Override
    public void login(String username, String pwd) {
        LoginRegRequestValue requestValue = new LoginRegRequestValue();
        requestValue.setAccount(username);
        requestValue.setPassword(pwd);
        if(!requestValue.checkInput()){
            switch (requestValue.getErrorStringRes()){
                case R.string.error_phone_empty:
                case R.string.error_phone_fail:
                    getView().usernameRequestFocus();
                    break;
                case R.string.error_auth_code_empty:
                case R.string.error_verify_fail:
                    getView().verifyRequestFocus();
                    break;
                case R.string.error_password_empty:
                case R.string.error_password_fail:
                    getView().passwordRequestFocus();
                    break;
            }
            getView().showToast(requestValue.getErrorStringRes());return;
        }
        getView().showWaitingDialog();
        loginUseCase.unSubscribe();
        loginUseCase.execute(new Subscriber<HttpRespose>() {
            @Override
            public void onCompleted() {
                getView().hideWaitingDialog();
            }

            @Override
            public void onError(Throwable e) {
                getView().hideWaitingDialog();
//                ErrorMessage errorMessage = ThrowableToErrorMessage.toErrorMessage(e, getView().getActivityContext());
//                if (errorMessage.getErrorProcessRunnable() != null)
//                    errorMessage.getErrorProcessRunnable().run();
                getView().onLoginFail();
            }

            @Override
            public void onNext(HttpRespose o) {
                getView().hideWaitingDialog();
                getView().showToast(o.getMsg());
                if (o.getCode() == 0) {
                    getView().onLoginSuccess(o);
                }
            }
        },requestValue);
    }

}
