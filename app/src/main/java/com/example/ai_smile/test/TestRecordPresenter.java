package com.example.ai_smile.test;

import com.example.ai_smile.data.bean.HttpRespose;
import com.example.ai_smile.data.bean.TestRecordBean;
import com.example.ai_smile.data.bean.TestResultBean;
import com.example.ai_smile.http.ErrorMessage;
import com.example.ai_smile.http.ThrowableToErrorMessage;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import java.util.List;
import rx.Subscriber;

public class TestRecordPresenter extends MvpNullObjectBasePresenter<TestRecordContract.View> implements TestRecordContract.Presenter {

    private TestRecordUseCase testRecordListUseCase;
    private TestInfoUseCase testInfoUseCase;


    public TestRecordPresenter(TestRecordUseCase testRecordListUseCase, TestInfoUseCase testInfoUseCase) {
        this.testRecordListUseCase = testRecordListUseCase;
        this.testInfoUseCase = testInfoUseCase;
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        testRecordListUseCase.unSubscribe();
        testInfoUseCase.unSubscribe();
    }

    @Override
    public void getTestRecordData(String macAddress) {
        RecordRequestValue requestValue = new RecordRequestValue();
        requestValue.setMacAddress(macAddress);
        getView().showWaitingDialog();
        testRecordListUseCase.execute(new Subscriber<HttpRespose<List<TestRecordBean>>>() {
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
            public void onNext(HttpRespose<List<TestRecordBean>> respose) {
                getView().hideWaitingDialog();
                if (respose.getCode() == 0) {
                    getView().onGetTestRecordListSuccess(respose);
                } else{
                    getView().showToast(respose.getMsg());
                }
            }
        }, requestValue);
    }

    @Override
    public void getTestRecordInfo(String recordId) {
        RecordRequestValue requestValue = new RecordRequestValue();
        requestValue.setId(recordId);
        getView().showWaitingDialog();
        testInfoUseCase.execute(new Subscriber<HttpRespose<List<TestResultBean>>>() {
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
            public void onNext(HttpRespose<List<TestResultBean>> respose) {
                getView().hideWaitingDialog();
                if (respose.getCode() == 0) {
                    getView().onGetTestRecordInfoSuccess(respose);
                } else{
                    getView().showToast(respose.getMsg());
                }
            }
        }, requestValue);
    }

}
