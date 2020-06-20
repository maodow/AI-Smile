package com.example.ai_smile.test;

import com.example.ai_smile.data.UserRepository;
import com.example.ai_smile.http.UseCase;
import rx.Observable;

/**
 * Created by Administrator on 2020/5/24.
 *
 */
public class FaceTestUseCase extends UseCase<FaceTestRequestValue> {

    private UserRepository mRepository;

    public FaceTestUseCase(UserRepository mRepository) {
        this.mRepository = mRepository;
    }

    @Override
    protected Observable buildUseCaseObservable(FaceTestRequestValue rv) {
        return mRepository.faceTest(rv.getMacAddress(), rv.getFilePath());
    }

}