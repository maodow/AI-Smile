package com.example.ai_smile.login;

import com.example.ai_smile.data.UserRepository;
import com.example.ai_smile.http.UseCase;
import rx.Observable;

public class LoginUseCase extends UseCase<LoginRegRequestValue> {

    UserRepository userRepository;

    public LoginUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected Observable buildUseCaseObservable(LoginRegRequestValue rv) {
        return userRepository.login(rv);
    }
}
