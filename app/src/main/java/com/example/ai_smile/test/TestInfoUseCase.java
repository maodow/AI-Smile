package com.example.ai_smile.test;

import com.example.ai_smile.data.UserRepository;
import com.example.ai_smile.http.UseCase;
import rx.Observable;

public class TestInfoUseCase extends UseCase<RecordRequestValue> {

    UserRepository userRepository;

    public TestInfoUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected Observable buildUseCaseObservable(RecordRequestValue rv) {
        return userRepository.getRecordInfo(rv.getId());
    }

}
