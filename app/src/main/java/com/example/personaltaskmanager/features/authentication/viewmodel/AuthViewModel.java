package com.example.personaltaskmanager.features.authentication.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;
import com.example.personaltaskmanager.features.authentication.domain.usecase.*;

/**
 * AuthViewModel
 * ----------------
 * Trung gian giữa UI và UseCases.
 * Không chứa logic UI và không thao tác DB trực tiếp.
 */
public class AuthViewModel extends ViewModel {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final ForgotPasswordUseCase forgotUseCase;

    public AuthViewModel(Context context) {

        AuthRepository repo = new AuthRepository(context);

        loginUseCase = new LoginUseCase(repo);
        registerUseCase = new RegisterUseCase(repo);
        logoutUseCase = new LogoutUseCase(repo);
        getCurrentUserUseCase = new GetCurrentUserUseCase(repo);
        forgotUseCase = new ForgotPasswordUseCase();
    }

    // ---- CHỈ SỬA HÀM NÀY ----
    public User login(String username, String password) {
        return loginUseCase.execute(username, password);
    }
    // -------------------------

    public boolean register(User user) {
        return registerUseCase.execute(user);
    }

    public void logout() {
        logoutUseCase.execute();
    }

    public User getCurrent() {
        return getCurrentUserUseCase.execute();
    }

    public boolean forgotPassword(String email) {
        return forgotUseCase.execute(email);
    }
}
