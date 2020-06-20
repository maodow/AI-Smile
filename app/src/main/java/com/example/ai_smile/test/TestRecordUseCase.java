package com.example.ai_smile.test;

import com.example.ai_smile.data.UserRepository;
import com.example.ai_smile.http.UseCase;

import rx.Observable;

public class TestRecordUseCase extends UseCase<RecordRequestValue> {

    UserRepository userRepository;

    public TestRecordUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected Observable buildUseCaseObservable(RecordRequestValue rv) {
        return userRepository.getTestRecordData(rv.getMacAddress());
    }

}
