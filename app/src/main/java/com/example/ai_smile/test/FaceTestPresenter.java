package com.example.ai_smile.test;

import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.TestResultBean;
import com.example.ai_smile.http.ErrorMessage;
import com.example.ai_smile.http.ThrowableToErrorMessage;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import java.util.List;
import rx.Subscriber;

public class FaceTestPresenter extends MvpNullObjectBasePresenter<FaceTestContract.View> implements FaceTestContract.Presenter {

    private FaceTestUseCase faceTestUseCase;


    public FaceTestPresenter(FaceTestUseCase faceTestUseCase) {
        this.faceTestUseCase = faceTestUseCase;
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        faceTestUseCase.unSubscribe();
    }

    @Override
    public void faceTest(String macAddress, String filePath) {
        FaceTestRequestValue requestValue = new FaceTestRequestValue();
        requestValue.setMacAddress(macAddress);
        requestValue.setFilePath(filePath);
        getView().showWaitingDialog();
        faceTestUseCase.execute(new Subscriber<HttpRespose<List<TestResultBean>>>() {
            @Override
            public void onCompleted() {
                getView().hideWaitingDialog();
            }

            @Override
            public void onError(Throwable e) {
                getView().hideWaitingDialog();
                ErrorMessage errorMessage = ThrowableToErrorMessage.toErrorMessage(e, getView().getActivityContext());
                if (errorMessage.getErrorProcessRunnable() != null)
                    errorMessage.getErrorProcessRunnable().run();
            }

            @Override
            public void onNext(HttpRespose<List<TestResultBean>> o) {
                getView().hideWaitingDialog();
                if (o.getCode() == 0) {
                    getView().onFaceTestSuccess(o);
                }
            }
        }, requestValue);
    }

}
